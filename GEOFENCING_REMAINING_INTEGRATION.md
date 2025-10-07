# Geofencing Integration - Remaining Steps

**Status:** ✅ Partial Integration Complete (Imports, Interface, Variables Added)
**Remaining:** Add methods and replace saveForm calls

---

## ✅ Already Applied (Steps 1-3)

- ✅ Imports added (Location, GeofenceFormHelper, LocationValidationDialogFragment, MapPoint)
- ✅ Interface added to class declaration (LocationValidationCallback)
- ✅ Class variables added (locationValidationOverridden, pendingFormSave fields)

---

## 🔧 Remaining Steps (4-6)

### Step 4: Add Validation Methods

**Location:** Add these methods BEFORE the existing `saveForm` method (around line ~1550)

**Copy and paste this entire block:**

```java
/**
 * Validate user location before saving/finalizing form
 * Only validates when form is being marked as complete (finalized)
 */
private void validateLocationBeforeSave(boolean exit, boolean complete, String updatedSaveName, boolean current) {
    // Only validate on finalization, not regular saves
    if (!complete) {
        // Not finalizing - proceed normally
        saveForm(exit, complete, updatedSaveName, current);
        return;
    }

    // Try to get current location
    Location currentLocation = getCurrentLocation();

    if (currentLocation == null) {
        // No location available - warn but allow save
        new MaterialAlertDialogBuilder(this)
            .setTitle("No GPS Location")
            .setMessage("GPS location is not available. Continue without location validation?")
            .setPositiveButton("Continue", (dialog, which) -> {
                saveForm(exit, complete, updatedSaveName, current);
            })
            .setNegativeButton("Cancel", null)
            .show();
        return;
    }

    // Store pending save parameters
    pendingFormSave = true;
    pendingFormSaveExit = exit;
    pendingFormSaveComplete = complete;
    pendingFormSaveName = updatedSaveName;
    pendingFormSaveCurrent = current;

    // Perform validation in background thread
    MapPoint mapPoint = new MapPoint(currentLocation.getLatitude(), currentLocation.getLongitude());

    new AsyncTask<Void, Void, GeofenceFormHelper.ValidationResult>() {
        @Override
        protected GeofenceFormHelper.ValidationResult doInBackground(Void... voids) {
            return GeofenceFormHelper.INSTANCE.validateLocationForUser(
                FormFillingActivity.this, mapPoint
            );
        }

        @Override
        protected void onPostExecute(GeofenceFormHelper.ValidationResult result) {
            handleValidationResult(result);
        }
    }.execute();
}

/**
 * Handle validation result on UI thread
 */
private void handleValidationResult(GeofenceFormHelper.ValidationResult result) {
    if (result.isValid() || locationValidationOverridden) {
        // Location valid or overridden - proceed with save
        proceedWithFormSave();
    } else {
        // Location invalid - show dialog
        boolean canOverride = GeofenceFormHelper.INSTANCE.canOverrideLocationRestrictions(this);
        LocationValidationDialogFragment dialog =
            LocationValidationDialogFragment.Companion.newInstance(result, canOverride);
        dialog.show(getSupportFragmentManager(), "location_validation");
    }
}

/**
 * Proceed with form save after validation
 */
private void proceedWithFormSave() {
    if (pendingFormSave) {
        saveForm(pendingFormSaveExit, pendingFormSaveComplete, pendingFormSaveName, pendingFormSaveCurrent);
        resetPendingSaveState();
    }
}

/**
 * Reset pending save state
 */
private void resetPendingSaveState() {
    pendingFormSave = false;
    pendingFormSaveExit = false;
    pendingFormSaveComplete = false;
    pendingFormSaveName = null;
    pendingFormSaveCurrent = false;
    locationValidationOverridden = false;
}

/**
 * Get current location from location manager
 * @return Current location or null if not available
 */
private Location getCurrentLocation() {
    try {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            // Try GPS first
            Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (gpsLocation != null) {
                return gpsLocation;
            }

            // Fall back to network location
            Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            return networkLocation;
        }
    } catch (SecurityException e) {
        Timber.e(e, "No permission to access location");
    }
    return null;
}
```

---

### Step 5: Add Callback Implementations

**Location:** Add these methods at the END of the class (before the final closing brace)

```java
// LocationValidationDialogFragment.LocationValidationCallback implementation
@Override
public void onOverrideLocation() {
    Timber.i("Location validation overridden by admin");
    locationValidationOverridden = true;
    proceedWithFormSave();
}

@Override
public void onCancelForm() {
    Timber.i("Form save cancelled due to location validation");
    showShortToast(this, "Form save cancelled - location validation failed");
    resetPendingSaveState();
}
```

---

### Step 6: Replace saveForm Calls

**Find and replace these 3 locations:**

**Location 1:** Search for line containing:
```java
saveForm(true, InstancesDaoHelper.isInstanceComplete(getFormController()), null, true);
```

Replace with:
```java
validateLocationBeforeSave(true, InstancesDaoHelper.isInstanceComplete(getFormController()), null, true);
```

**Location 2:** Search for line containing:
```java
saveForm(false, InstancesDaoHelper.isInstanceComplete(getFormController()), null, true);
```

Replace with:
```java
validateLocationBeforeSave(false, InstancesDaoHelper.isInstanceComplete(getFormController()), null, true);
```

**Location 3:** Search for line containing:
```java
markAsFinalized -> saveForm(true, markAsFinalized, saveName, false)
```

Replace with:
```java
markAsFinalized -> validateLocationBeforeSave(true, markAsFinalized, saveName, false)
```

---

## 🧪 Testing After Integration

### 1. Build
```bash
./gradlew :collect_app:assembleDebug
```

### 2. Test Scenarios

**Scenario 1: State User in Correct State**
- Login: Kaduna state user
- Location: Mock to Kaduna (10.5230, 7.4390)
- Fill form and finalize
- **Expected:** Form saves without validation dialog

**Scenario 2: State User in Wrong State**
- Login: Kaduna state user
- Location: Mock to Kano (12.0022, 8.5920)
- Fill form and finalize
- **Expected:** Validation error, no override, cannot save

**Scenario 3: Admin Override**
- Login: Federal admin
- Location: Mock to Lagos (6.5244, 3.3792) - outside boundaries
- Fill form and finalize
- **Expected:** Validation dialog with override button, can save

---

## 📊 Integration Summary

| Step | Status | Description |
|------|--------|-------------|
| 1 | ✅ **Done** | Imports added |
| 2 | ✅ **Done** | Interface added to class |
| 3 | ✅ **Done** | Variables added |
| 4 | ⏳ **Pending** | Add validation methods (copy code above) |
| 5 | ⏳ **Pending** | Add callback implementations (copy code above) |
| 6 | ⏳ **Pending** | Replace 3 saveForm calls |

---

## 💡 Quick Instructions

1. **Open FormFillingActivity.java** in Android Studio
2. **Find line ~1550** (the saveForm method)
3. **Paste Step 4 code** ABOVE the saveForm method
4. **Scroll to bottom** of class
5. **Paste Step 5 code** BEFORE final closing brace `}`
6. **Use Find (Ctrl+F)** to find the 3 saveForm calls in Step 6
7. **Replace** each with validateLocationBeforeSave
8. **Build** and test

---

**Estimated Time:** 10-15 minutes

**Files to Modify:** 1 (FormFillingActivity.java)

**Lines to Add:** ~120 lines

---

**Status:** ⏳ **Ready to Complete**

The hard part (imports, interface, variables) is done. Just add the methods and replace the calls!

---

**END OF REMAINING INTEGRATION STEPS**
