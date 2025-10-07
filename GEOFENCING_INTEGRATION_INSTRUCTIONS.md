# Geofencing Integration - Detailed Instructions

**File:** FormFillingActivity.java
**Lines to Modify:** ~1550-1563 (saveForm method)
**Status:** Ready to integrate

---

## 🎯 Integration Strategy

Since `FormFillingActivity.java` is complex (2000+ lines, multiple interfaces), we'll use a **minimal intervention** approach:

1. Add imports at top of file
2. Implement callback interface
3. Add validation method
4. Modify existing `saveForm` method to call validation

---

## 📝 Step-by-Step Integration

### Step 1: Add Imports (Lines ~38-150)

**Add these imports after existing import statements:**

```java
// Add after line 38 (android.location.LocationManager)
import android.location.Location;

// Add after line 150 (around other org.odk imports)
import org.odk.collect.android.geofencing.GeofenceFormHelper;
import org.odk.collect.android.geofencing.LocationValidationDialogFragment;
import org.odk.collect.maps.MapPoint;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;
```

---

### Step 2: Implement Callback Interface (Line ~231)

**Find the class declaration (line 226):**

```java
public class FormFillingActivity extends LocalizedActivity implements AnimationListener,
        FormLoaderListener, AdvanceToNextListener, SwipeHandler.OnSwipeListener,
        SavepointListener, NumberPickerDialog.NumberPickerListener,
        RankingWidgetDialog.RankingListener, SaveFormIndexTask.SaveFormIndexListener,
        WidgetValueChangedListener, ScreenContext, FormLoadingDialogFragment.FormLoadingDialogFragmentListener,
        AudioControllerView.SwipableParent, FormIndexAnimationHandler.Listener,
```

**Add the callback interface:**

```java
public class FormFillingActivity extends LocalizedActivity implements AnimationListener,
        FormLoaderListener, AdvanceToNextListener, SwipeHandler.OnSwipeListener,
        SavepointListener, NumberPickerDialog.NumberPickerListener,
        RankingWidgetDialog.RankingListener, SaveFormIndexTask.SaveFormIndexListener,
        WidgetValueChangedListener, ScreenContext, FormLoadingDialogFragment.FormLoadingDialogFragmentListener,
        AudioControllerView.SwipableParent, FormIndexAnimationHandler.Listener,
        LocationValidationDialogFragment.LocationValidationCallback,
```

---

### Step 3: Add Class Variables (After line ~400)

**Add these private variables:**

```java
// Geofencing validation state
private boolean locationValidationOverridden = false;
private boolean pendingFormSave = false;
private boolean pendingFormSaveExit = false;
private boolean pendingFormSaveComplete = false;
private String pendingFormSaveName = null;
private boolean pendingFormSaveCurrent = false;
```

---

### Step 4: Add Validation Method (Before saveForm method, around line ~1545)

**Add this new method:**

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

    // Perform validation in coroutine
    MapPoint mapPoint = new MapPoint(currentLocation.getLatitude(), currentLocation.getLongitude());

    GlobalScope.INSTANCE.launch(Dispatchers.getMain(), (scope, continuation) -> {
        GeofenceFormHelper.ValidationResult result =
            GeofenceFormHelper.INSTANCE.validateLocationForUser(this, mapPoint);

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
        return null;
    });
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

### Step 5: Implement Callback Methods (Add at end of class, before closing brace)

**Add these interface implementations:**

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

### Step 6: Replace `saveForm` Calls

**Find these 3 locations where `saveForm` is called with `complete=true` or finalization:**

**Location 1 (Line ~401):**
```java
// OLD:
saveForm(true, InstancesDaoHelper.isInstanceComplete(getFormController()), null, true);

// NEW:
validateLocationBeforeSave(true, InstancesDaoHelper.isInstanceComplete(getFormController()), null, true);
```

**Location 2 (Line ~499):**
```java
// OLD:
saveForm(false, InstancesDaoHelper.isInstanceComplete(getFormController()), null, true);

// NEW:
validateLocationBeforeSave(false, InstancesDaoHelper.isInstanceComplete(getFormController()), null, true);
```

**Location 3 (Line ~1230):**
```java
// OLD:
markAsFinalized -> saveForm(true, markAsFinalized, saveName, false)

// NEW:
markAsFinalized -> validateLocationBeforeSave(true, markAsFinalized, saveName, false)
```

---

## 🧪 Testing the Integration

### Test Scenario 1: State User in Correct State
1. Login as state user (e.g., Kaduna)
2. Fill form in Kaduna
3. Click "Finalize Form"
4. **Expected:** Form saves without validation dialog

### Test Scenario 2: State User in Wrong State
1. Login as state user (Kaduna)
2. Mock location to Kano
3. Fill form
4. Click "Finalize Form"
5. **Expected:** Validation dialog appears, no override button, cannot save

### Test Scenario 3: Federal Admin Override
1. Login as federal admin
2. Mock location to Lagos (outside boundaries)
3. Fill form
4. Click "Finalize Form"
5. **Expected:** Validation dialog with "Override & Continue" button
6. Click override
7. **Expected:** Form saves successfully

### Test Scenario 4: No GPS Location
1. Disable GPS
2. Fill form
3. Click "Finalize Form"
4. **Expected:** Warning dialog "No GPS Location", option to continue

---

## 📊 Impact Analysis

### Lines Changed: ~150 lines added
- Imports: ~5 lines
- Class declaration: 1 line modified
- Variables: 6 lines
- Methods: ~130 lines
- Callbacks: ~10 lines
- Method call replacements: 3 locations

### Files Modified: 1
- FormFillingActivity.java

### Risk Level: **Medium**
- Adding validation logic to existing form save flow
- Using coroutines in Java (requires kotlinx dependency)
- Minimal changes to existing logic (only wrapping calls)

---

## ⚠️ Important Notes

1. **Timber Import:** Make sure `Timber` is imported (should already be in file)
2. **Kotlin Coroutines:** The `GlobalScope.INSTANCE.launch` requires Kotlin coroutines library
3. **Location Permission:** Ensure app has location permission granted
4. **Testing:** Test thoroughly before deploying - form saving is critical

---

## 🔧 Alternative: Simpler Approach (If coroutines are problematic)

If the coroutine approach doesn't compile, use AsyncTask instead:

```java
private void validateLocationBeforeSave(boolean exit, boolean complete, String updatedSaveName, boolean current) {
    if (!complete) {
        saveForm(exit, complete, updatedSaveName, current);
        return;
    }

    Location currentLocation = getCurrentLocation();
    if (currentLocation == null) {
        // Show warning and proceed
        saveForm(exit, complete, updatedSaveName, current);
        return;
    }

    // Store parameters
    pendingFormSaveExit = exit;
    pendingFormSaveComplete = complete;
    pendingFormSaveName = updatedSaveName;
    pendingFormSaveCurrent = current;

    // Run validation in background
    new AsyncTask<Void, Void, GeofenceFormHelper.ValidationResult>() {
        @Override
        protected GeofenceFormHelper.ValidationResult doInBackground(Void... voids) {
            MapPoint point = new MapPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
            return GeofenceFormHelper.INSTANCE.validateLocationForUser(
                FormFillingActivity.this, point
            );
        }

        @Override
        protected void onPostExecute(GeofenceFormHelper.ValidationResult result) {
            handleValidationResult(result);
        }
    }.execute();
}

private void handleValidationResult(GeofenceFormHelper.ValidationResult result) {
    if (result.isValid()) {
        proceedWithFormSave();
    } else {
        boolean canOverride = GeofenceFormHelper.INSTANCE.canOverrideLocationRestrictions(this);
        LocationValidationDialogFragment dialog =
            LocationValidationDialogFragment.Companion.newInstance(result, canOverride);
        dialog.show(getSupportFragmentManager(), "location_validation");
    }
}
```

---

## ✅ Final Checklist

Before committing:
- [ ] All imports added
- [ ] Interface implemented in class declaration
- [ ] Class variables added
- [ ] Validation method added
- [ ] Callback methods implemented
- [ ] All 3 `saveForm` calls replaced
- [ ] Code compiles without errors
- [ ] Tested on device with GPS
- [ ] Tested state user boundary validation
- [ ] Tested admin override flow

---

**Status:** 📝 **Ready to Implement**

**Estimated Time:** 30-45 minutes

**Next Step:** Apply these changes to FormFillingActivity.java

---

**END OF INTEGRATION INSTRUCTIONS**
