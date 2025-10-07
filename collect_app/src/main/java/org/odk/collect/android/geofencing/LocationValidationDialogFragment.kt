package org.odk.collect.android.geofencing

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.odk.collect.android.R

/**
 * Dialog fragment to display location validation errors and allow admin override
 */
class LocationValidationDialogFragment : DialogFragment() {

    interface LocationValidationCallback {
        fun onOverrideLocation()
        fun onCancelForm()
    }

    private var callback: LocationValidationCallback? = null
    private var validationResult: GeofenceFormHelper.ValidationResult? = null
    private var canOverride: Boolean = false

    companion object {
        private const val ARG_ERROR_MESSAGE = "error_message"
        private const val ARG_REQUIRES_OVERRIDE = "requires_override"
        private const val ARG_CAN_OVERRIDE = "can_override"

        fun newInstance(
            validationResult: GeofenceFormHelper.ValidationResult,
            canOverride: Boolean
        ): LocationValidationDialogFragment {
            return LocationValidationDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ERROR_MESSAGE, validationResult.errorMessage)
                    putBoolean(ARG_REQUIRES_OVERRIDE, validationResult.requiresOverride)
                    putBoolean(ARG_CAN_OVERRIDE, canOverride)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LocationValidationCallback) {
            callback = context
        } else if (parentFragment is LocationValidationCallback) {
            callback = parentFragment as LocationValidationCallback
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val errorMessage = arguments?.getString(ARG_ERROR_MESSAGE) ?: "Location validation failed"
        val requiresOverride = arguments?.getBoolean(ARG_REQUIRES_OVERRIDE) ?: false
        val canOverride = arguments?.getBoolean(ARG_CAN_OVERRIDE) ?: false

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Location Validation Error")
            .setMessage(errorMessage)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setCancelable(false)

        if (requiresOverride && canOverride) {
            // Admin can override
            builder.setPositiveButton("Override & Continue") { _, _ ->
                callback?.onOverrideLocation()
                dismiss()
            }
            builder.setNegativeButton("Cancel") { _, _ ->
                callback?.onCancelForm()
                dismiss()
            }
        } else {
            // Cannot override - only option is to cancel
            builder.setPositiveButton("OK") { _, _ ->
                callback?.onCancelForm()
                dismiss()
            }
        }

        return builder.create()
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }
}
