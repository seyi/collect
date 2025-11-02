package org.odk.collect.android.cosmosdb

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.odk.collect.android.cosmosdb.models.PendingSubmission
import org.odk.collect.android.cosmosdb.models.SyncStatus
import timber.log.Timber

/**
 * SQLite database for storing pending Cosmos DB submissions
 * Manages offline queue and retry logic
 */
class PendingSubmissionStore private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cosmos_sync.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_PENDING = "pending_submissions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_SUBMISSION_ID = "submission_id"
        private const val COLUMN_INSTANCE_PATH = "instance_path"
        private const val COLUMN_FORM_ID = "form_id"
        private const val COLUMN_STATE = "state"
        private const val COLUMN_STATUS = "status"
        private const val COLUMN_RETRY_COUNT = "retry_count"
        private const val COLUMN_LAST_ERROR_CODE = "last_error_code"
        private const val COLUMN_LAST_ERROR_MESSAGE = "last_error_message"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_LAST_ATTEMPT_AT = "last_attempt_at"
        private const val COLUMN_SYNCED_AT = "synced_at"

        @Volatile
        private var instance: PendingSubmissionStore? = null

        @JvmStatic
        fun getInstance(context: Context): PendingSubmissionStore {
            return instance ?: synchronized(this) {
                instance ?: PendingSubmissionStore(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_PENDING (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SUBMISSION_ID TEXT NOT NULL UNIQUE,
                $COLUMN_INSTANCE_PATH TEXT NOT NULL,
                $COLUMN_FORM_ID TEXT NOT NULL,
                $COLUMN_STATE TEXT NOT NULL,
                $COLUMN_STATUS TEXT NOT NULL,
                $COLUMN_RETRY_COUNT INTEGER DEFAULT 0,
                $COLUMN_LAST_ERROR_CODE TEXT,
                $COLUMN_LAST_ERROR_MESSAGE TEXT,
                $COLUMN_CREATED_AT INTEGER NOT NULL,
                $COLUMN_LAST_ATTEMPT_AT INTEGER,
                $COLUMN_SYNCED_AT INTEGER
            )
        """.trimIndent()

        db.execSQL(createTable)

        // Create index on status for efficient querying
        db.execSQL("CREATE INDEX idx_status ON $TABLE_PENDING($COLUMN_STATUS)")
        db.execSQL("CREATE INDEX idx_submission_id ON $TABLE_PENDING($COLUMN_SUBMISSION_ID)")

        Timber.d("PendingSubmissionStore database created")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // For now, just drop and recreate
        // In production, implement proper migration
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PENDING")
        onCreate(db)
    }

    /**
     * Add a submission to the pending queue
     */
    fun addPending(
        submissionId: String,
        instancePath: String,
        formId: String,
        state: String
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SUBMISSION_ID, submissionId)
            put(COLUMN_INSTANCE_PATH, instancePath)
            put(COLUMN_FORM_ID, formId)
            put(COLUMN_STATE, state)
            put(COLUMN_STATUS, SyncStatus.PENDING.name)
            put(COLUMN_RETRY_COUNT, 0)
            put(COLUMN_CREATED_AT, System.currentTimeMillis())
        }

        return try {
            val id = db.insertWithOnConflict(TABLE_PENDING, null, values, SQLiteDatabase.CONFLICT_REPLACE)
            Timber.d("Added pending submission: $submissionId (id=$id)")
            id
        } catch (e: Exception) {
            Timber.e(e, "Error adding pending submission: $submissionId")
            -1
        }
    }

    /**
     * Get all pending submissions that need to be synced
     */
    fun getPendingSubmissions(): List<PendingSubmission> {
        val submissions = mutableListOf<PendingSubmission>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_PENDING,
            null,
            "$COLUMN_STATUS IN (?, ?)",
            arrayOf(SyncStatus.PENDING.name, SyncStatus.FAILED.name),
            null,
            null,
            "$COLUMN_CREATED_AT ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val submission = cursorToSubmission(it)
                if (submission.shouldRetry() || submission.status == SyncStatus.PENDING) {
                    submissions.add(submission)
                }
            }
        }

        Timber.d("Found ${submissions.size} pending submissions")
        return submissions
    }

    /**
     * Update submission status to syncing
     */
    fun markAsSyncing(submissionId: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STATUS, SyncStatus.SYNCING.name)
            put(COLUMN_LAST_ATTEMPT_AT, System.currentTimeMillis())
        }

        db.update(TABLE_PENDING, values, "$COLUMN_SUBMISSION_ID = ?", arrayOf(submissionId))
        Timber.d("Marked submission as syncing: $submissionId")
    }

    /**
     * Update submission status to synced
     */
    fun markAsSynced(submissionId: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STATUS, SyncStatus.SYNCED.name)
            put(COLUMN_SYNCED_AT, System.currentTimeMillis())
        }

        db.update(TABLE_PENDING, values, "$COLUMN_SUBMISSION_ID = ?", arrayOf(submissionId))
        Timber.d("Marked submission as synced: $submissionId")
    }

    /**
     * Update submission status to failed
     */
    fun markAsFailed(submissionId: String, errorCode: String, errorMessage: String) {
        val db = writableDatabase

        // First get current retry count
        val cursor = db.query(
            TABLE_PENDING,
            arrayOf(COLUMN_RETRY_COUNT),
            "$COLUMN_SUBMISSION_ID = ?",
            arrayOf(submissionId),
            null,
            null,
            null
        )

        val retryCount = cursor.use {
            if (it.moveToFirst()) {
                it.getInt(it.getColumnIndexOrThrow(COLUMN_RETRY_COUNT))
            } else {
                0
            }
        }

        val values = ContentValues().apply {
            put(COLUMN_STATUS, SyncStatus.FAILED.name)
            put(COLUMN_RETRY_COUNT, retryCount + 1)
            put(COLUMN_LAST_ERROR_CODE, errorCode)
            put(COLUMN_LAST_ERROR_MESSAGE, errorMessage)
            put(COLUMN_LAST_ATTEMPT_AT, System.currentTimeMillis())
        }

        db.update(TABLE_PENDING, values, "$COLUMN_SUBMISSION_ID = ?", arrayOf(submissionId))
        Timber.d("Marked submission as failed: $submissionId (retry count: ${retryCount + 1})")
    }

    /**
     * Get submission by ID
     */
    fun getSubmission(submissionId: String): PendingSubmission? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PENDING,
            null,
            "$COLUMN_SUBMISSION_ID = ?",
            arrayOf(submissionId),
            null,
            null,
            null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                cursorToSubmission(it)
            } else {
                null
            }
        }
    }

    /**
     * Delete submission from queue
     */
    fun deleteSubmission(submissionId: String) {
        val db = writableDatabase
        db.delete(TABLE_PENDING, "$COLUMN_SUBMISSION_ID = ?", arrayOf(submissionId))
        Timber.d("Deleted submission from queue: $submissionId")
    }

    /**
     * Get count of pending submissions
     */
    fun getPendingCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_PENDING WHERE $COLUMN_STATUS IN (?, ?)",
            arrayOf(SyncStatus.PENDING.name, SyncStatus.FAILED.name)
        )

        return cursor.use {
            if (it.moveToFirst()) {
                it.getInt(0)
            } else {
                0
            }
        }
    }

    /**
     * Clear old synced submissions (older than 7 days)
     */
    fun clearOldSynced() {
        val db = writableDatabase
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)

        val count = db.delete(
            TABLE_PENDING,
            "$COLUMN_STATUS = ? AND $COLUMN_SYNCED_AT < ?",
            arrayOf(SyncStatus.SYNCED.name, sevenDaysAgo.toString())
        )

        Timber.d("Cleared $count old synced submissions")
    }

    /**
     * Convert cursor to PendingSubmission object
     */
    private fun cursorToSubmission(cursor: Cursor): PendingSubmission {
        return PendingSubmission(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            submissionId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_ID)),
            instancePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_PATH)),
            formId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FORM_ID)),
            state = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATE)),
            status = SyncStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))),
            retryCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RETRY_COUNT)),
            lastErrorCode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_ERROR_CODE)),
            lastErrorMessage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_ERROR_MESSAGE)),
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
            lastAttemptAt = if (cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_LAST_ATTEMPT_AT))) {
                null
            } else {
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LAST_ATTEMPT_AT))
            },
            syncedAt = if (cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_SYNCED_AT))) {
                null
            } else {
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SYNCED_AT))
            }
        )
    }
}
