package org.odk.collect.android.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class CSVDataProvider {
    private static final String CSV_DATA_KEY = "csv_data_json";
    private static final String CSV_LOADED_KEY = "csv_loaded";

    private static volatile CSVDataProvider instance; // Made volatile for thread safety
    private Context context;
    private List<Map<String, String>> csvData;
    private Map<String, Integer> headerIndexMap;
    private final Object lock = new Object(); // For thread safety

    private CSVDataProvider(Context context) {
        this.context = context.getApplicationContext();
        this.csvData = new ArrayList<>();
        this.headerIndexMap = new HashMap<>();
        loadFromSharedPreferences();
    }

    public static CSVDataProvider getInstance(Context context) {
        if (instance == null) {
            synchronized (CSVDataProvider.class) {
                if (instance == null) {
                    instance = new CSVDataProvider(context);
                }
            }
        }
        return instance;
    }

    /**
     * IMPROVED: Save CSV data with better error handling
     */
    public boolean saveCSVData(List<Map<String, String>> data) {
        synchronized (lock) {
            try {
                if (data == null || data.isEmpty()) {
                    Timber.w("Attempting to save null or empty CSV data");
                    return false;
                }

                JSONArray jsonArray = new JSONArray();
                for (Map<String, String> row : data) {
                    JSONObject jsonRow = new JSONObject();
                    for (Map.Entry<String, String> entry : row.entrySet()) {
                        // Handle null values properly
                        String value = entry.getValue();
                        jsonRow.put(entry.getKey(), value != null ? value : "");
                    }
                    jsonArray.put(jsonRow);
                }

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                boolean saveSuccess = prefs.edit()
                        .putString(CSV_DATA_KEY, jsonArray.toString())
                        .putBoolean(CSV_LOADED_KEY, true)
                        .commit();

                if (saveSuccess) {
                    this.csvData = new ArrayList<>(data);
                    updateHeaderIndexMap();
                    Timber.d("CSV data saved successfully: %d rows", data.size());
                    return true;
                } else {
                    Timber.e("Failed to save CSV data to SharedPreferences");
                    return false;
                }
            } catch (Exception e) {
                Timber.e(e, "Error saving CSV data");
                return false;
            }
        }
    }

    /**
     * IMPROVED: Load CSV data with better error handling
     */
    public void loadCSVFromJson(String jsonString) {
        synchronized (lock) {
            try {
                if (jsonString == null || jsonString.trim().isEmpty()) {
                    Timber.w("JSON string is null or empty");
                    return;
                }

                JSONArray jsonArray = new JSONArray(jsonString);
                csvData.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonRow = jsonArray.getJSONObject(i);
                    Map<String, String> row = new HashMap<>();

                    JSONArray keys = jsonRow.names();
                    if (keys != null) {
                        for (int j = 0; j < keys.length(); j++) {
                            String key = keys.getString(j);
                            String value = jsonRow.optString(key, ""); // Use optString with default
                            row.put(key, value);
                        }
                    }
                    csvData.add(row);
                }

                updateHeaderIndexMap();
                Timber.d("CSV data loaded from JSON: %d rows", csvData.size());

            } catch (Exception e) {
                Timber.e(e, "Error loading CSV from JSON");
                csvData.clear(); // Clear on error to maintain consistency
                headerIndexMap.clear();
            }
        }
    }

    /**
     * Load CSV data from SharedPreferences
     */
    private void loadFromSharedPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String csvJson = prefs.getString(CSV_DATA_KEY, null);

        if (csvJson != null && !csvJson.trim().isEmpty()) {
            loadCSVFromJson(csvJson);
        } else {
            Timber.d("No CSV data found in SharedPreferences");
        }
    }

    /**
     * FIXED: Get value from CSV by column name and row selector
     * Now returns empty string instead of null for consistency
     */
    public String getCSVValue(String columnName, String rowSelectorValue) {
        synchronized (lock) {
            if (csvData.isEmpty()) {
                Timber.w("CSV data is empty");
                return "";
            }

            if (columnName == null || columnName.trim().isEmpty()) {
                Timber.w("Column name is null or empty");
                return "";
            }

            // Normalize column name
            String normalizedColumnName = normalizeColumnName(columnName);
            String actualColumnName = findActualColumnName(normalizedColumnName);

            if (actualColumnName == null) {
                Timber.w("Column not found: %s", columnName);
                return "";
            }

            // If no row selector, use first row
            if (rowSelectorValue == null || rowSelectorValue.trim().isEmpty()) {
                String value = csvData.get(0).get(actualColumnName);
                return value != null ? value : "";
            }

            // IMPROVED: Find row matching selector in specific columns only
            // Look for the selector value in key identification columns first
            String[] keyColumns = {"ActivityID", "Component Code", "Site", "ID"};

            for (Map<String, String> row : csvData) {
                // First try key columns
                for (String keyCol : keyColumns) {
                    String keyColActual = findActualColumnName(keyCol);
                    if (keyColActual != null) {
                        String value = row.get(keyColActual);
                        if (value != null && value.equals(rowSelectorValue)) {
                            String result = row.get(actualColumnName);
                            return result != null ? result : "";
                        }
                    }
                }

                // Then try all columns as fallback
                for (String value : row.values()) {
                    if (value != null && value.equals(rowSelectorValue)) {
                        String result = row.get(actualColumnName);
                        return result != null ? result : "";
                    }
                }
            }

            // Fallback to first row
            String value = csvData.get(0).get(actualColumnName);
            return value != null ? value : "";
        }
    }

    /**
     * IMPROVED: Get value by row index with better validation
     */
    public String getCSVValue(String columnName, int rowIndex) {
        synchronized (lock) {
            if (csvData.isEmpty()) {
                Timber.w("CSV data is empty");
                return "";
            }

            if (rowIndex < 0 || rowIndex >= csvData.size()) {
                Timber.w("Row index out of bounds: %d (max: %d)", rowIndex, csvData.size() - 1);
                return "";
            }

            if (columnName == null || columnName.trim().isEmpty()) {
                Timber.w("Column name is null or empty");
                return "";
            }

            // Normalize and find actual column name
            String normalizedColumnName = normalizeColumnName(columnName);
            String actualColumnName = findActualColumnName(normalizedColumnName);

            if (actualColumnName == null) {
                Timber.w("Column not found: %s. Available columns: %s",
                        columnName, String.join(", ", getColumnNames()));
                return "";
            }

            String value = csvData.get(rowIndex).get(actualColumnName);
            return value != null ? value : "";
        }
    }

    /**
     * NEW: Normalize column names for flexible matching
     */
    private String normalizeColumnName(String columnName) {
        return columnName.trim()
                .toLowerCase()
                .replaceAll("\\s+", " ") // Normalize whitespace
                .replace("_", " ")       // Convert underscores to spaces
                .replace("-", " ");      // Convert hyphens to spaces
    }

    /**
     * NEW: Find actual column name with flexible matching
     */
    private String findActualColumnName(String normalizedTarget) {
        if (csvData.isEmpty()) {
            return null;
        }

        Map<String, String> firstRow = csvData.get(0);

        // First try exact match
        for (String actualColumn : firstRow.keySet()) {
            if (normalizeColumnName(actualColumn).equals(normalizedTarget)) {
                return actualColumn;
            }
        }

        // Then try partial matches
        for (String actualColumn : firstRow.keySet()) {
            String normalizedActual = normalizeColumnName(actualColumn);
            if (normalizedActual.contains(normalizedTarget) ||
                    normalizedTarget.contains(normalizedActual)) {
                return actualColumn;
            }
        }

        return null;
    }

    /**
     * IMPROVED: Check if CSV is loaded with better validation
     */
    public boolean isCSVLoaded() {
        synchronized (lock) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean flagSet = prefs.getBoolean(CSV_LOADED_KEY, false);
            boolean hasData = !csvData.isEmpty();

            if (flagSet && !hasData) {
                Timber.w("CSV loaded flag is true but no data in memory - reloading");
                loadFromSharedPreferences();
                hasData = !csvData.isEmpty();
            }

            return flagSet && hasData;
        }
    }

    public int getRowCount() {
        synchronized (lock) {
            return csvData.size();
        }
    }

    private void updateHeaderIndexMap() {
        headerIndexMap.clear();
        if (!csvData.isEmpty()) {
            Map<String, String> firstRow = csvData.get(0);
            int index = 0;
            for (String key : firstRow.keySet()) {
                headerIndexMap.put(key, index++);
            }
        }
    }

    /**
     * IMPROVED: Clear CSV data with better error handling
     */
    public boolean clearCSVData() {
        synchronized (lock) {
            try {
                // Clear in-memory data
                csvData.clear();
                headerIndexMap.clear();

                // Clear SharedPreferences
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                boolean success = prefs.edit()
                        .remove(CSV_DATA_KEY)
                        .putBoolean(CSV_LOADED_KEY, false)
                        .commit();

                if (success) {
                    Timber.d("CSV data cleared from memory and SharedPreferences");
                } else {
                    Timber.e("Failed to clear CSV data from SharedPreferences");
                }

                return success;

            } catch (Exception e) {
                Timber.e(e, "Error clearing CSV data");
                return false;
            }
        }
    }

    /**
     * ENHANCED: Get CSV data summary for debugging
     */
    public String getCSVSummary() {
        synchronized (lock) {
            if (csvData.isEmpty()) {
                return "No CSV data loaded";
            }

            StringBuilder summary = new StringBuilder();
            summary.append("CSV Summary:\n");
            summary.append("Rows: ").append(csvData.size()).append("\n");
            summary.append("Columns: ").append(String.join(", ", getColumnNames())).append("\n");

            // Add first row sample
            if (!csvData.isEmpty()) {
                Map<String, String> firstRow = csvData.get(0);
                summary.append("Sample data (first row):\n");
                for (Map.Entry<String, String> entry : firstRow.entrySet()) {
                    String value = entry.getValue();
                    if (value != null && value.length() > 50) {
                        value = value.substring(0, 47) + "...";
                    }
                    summary.append("  ").append(entry.getKey()).append(": ").append(value).append("\n");
                }
            }

            return summary.toString();
        }
    }

    public String[] getColumnNames() {
        synchronized (lock) {
            if (csvData.isEmpty()) {
                return new String[0];
            }

            Map<String, String> firstRow = csvData.get(0);
            return firstRow.keySet().toArray(new String[0]);
        }
    }

    /**
     * IMPROVED: Check if column exists with flexible matching
     */
    public boolean hasColumn(String columnName) {
        synchronized (lock) {
            if (csvData.isEmpty() || columnName == null) {
                return false;
            }

            String normalizedTarget = normalizeColumnName(columnName);
            String actualColumn = findActualColumnName(normalizedTarget);
            return actualColumn != null;
        }
    }

    /**
     * NEW: Get all data for debugging
     */
    public List<Map<String, String>> getAllData() {
        synchronized (lock) {
            return new ArrayList<>(csvData); // Return copy to prevent external modification
        }
    }

    /**
     * NEW: Validate CSV data integrity
     */
    public boolean validateDataIntegrity() {
        synchronized (lock) {
            if (csvData.isEmpty()) {
                return false;
            }

            // Check if all rows have the same columns
            String[] expectedColumns = getColumnNames();
            for (Map<String, String> row : csvData) {
                if (row.keySet().size() != expectedColumns.length) {
                    Timber.w("Row has different number of columns than expected");
                    return false;
                }
            }

            return true;
        }
    }
}