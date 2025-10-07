# Geofencing Phase 3 - Integration Guide

**Date:** October 7, 2025
**Purpose:** Step-by-step guide to integrate geofencing with ODK Collect forms

---

## 🎯 Integration Strategy

Since form integration requires deep knowledge of ODK Collect's architecture, this guide provides:

1. **Helper utilities** (✅ Ready)
2. **Integration points** (documented below)
3. **Code snippets** for each integration
4. **Testing approach**

---

## ✅ What's Ready

- `GeofenceFormHelper` - Auto-populate and validation logic
- `LocationValidationDialogFragment` - Validation UI
- `GeoFenceManager` - Core geofencing engine

---

## 📍 Integration Points

### Integration Point 1: Auto-Populate on Location Acquired

**When:** GPS location is obtained during form filling
**Where:** `FormFillingActivity` or location widget handlers
**What:** Call `GeofenceFormHelper.autoPopulateLocationFields()`

**Code Snippet:**

```java
// In FormFillingActivity.java or similar
import org.odk.collect.android.geofencing.GeofenceFormHelper;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;
import kotlinx.coroutines.launch;

private void onLocationAcquired(Location location) {
    MapPoint mapPoint = new MapPoint(location.getLatitude(), location.getLongitude());

    GlobalScope.INSTANCE.launch(Dispatchers.getMain(), (coroutineScope, continuation) -> {
        GeofenceFormHelper.LocationFieldValues fieldValues =
            GeofenceFormHelper.INSTANCE.autoPopulateLocationFields(this, mapPoint);

        if (fieldValues.isWithinBoundaries()) {
            // Populate form fields
            autoFillLocationFields(fieldValues);
        } else {
            showToast(fieldValues.getErrorMessage());
        }
        return null;
    });
}

private void autoFillLocationFields(GeofenceFormHelper.LocationFieldValues fieldValues) {
    // Find and populate state field
    if (fieldValues.getState() != null) {
        setAnswerForField("state", fieldValues.getState());
    }

    // Find and populate LGA field
    if (fieldValues.getLga() != null) {
        setAnswerForField("lga", fieldValues.getLga());
    }

    // Find and populate strategic catchment field
    if (fieldValues.getStrategicCatchment() != null) {
        setAnswerForField("strategic_catchment", fieldValues.getStrategicCatchment());
    }

    // Find and populate micro catchment field
    if (fieldValues.getMicroCatchment() != null) {
        setAnswerForField("micro_catchment", fieldValues.getMicroCatchment());
    }
}
```

**Alternative: Use Field Name Mapping**

```java
private void autoFillLocationFields(GeofenceFormHelper.LocationFieldValues fieldValues) {
    // Get all form fields
    FormEntryPrompt[] prompts = getFormEntryPrompts();

    for (FormEntryPrompt prompt : prompts) {
        String fieldName = prompt.getQuestionText(); // or getFieldName()
        String value = GeofenceFormHelper.INSTANCE.mapFieldValue(fieldName, fieldValues);

        if (value != null) {
            // Set answer and make read-only
            setAnswer(prompt, value);
            prompt.setReadOnly(true); // Lock auto-populated fields
        }
    }
}
```

---

### Integration Point 2: Pre-Submission Validation

**When:** User clicks "Finalize Form" or "Save & Exit"
**Where:** Form finalization/submission handler
**What:** Validate location before allowing submission

**Code Snippet:**

```java
// In form submission handler
import org.odk.collect.android.geofencing.LocationValidationDialogFragment;

public class FormFillingActivity extends AppCompatActivity
    implements LocationValidationDialogFragment.LocationValidationCallback {

    private Location currentLocation;
    private boolean locationOverridden = false;

    private void finalizeForm() {
        if (currentLocation == null) {
            showToast("Waiting for GPS location...");
            return;
        }

        MapPoint mapPoint = new MapPoint(
            currentLocation.getLatitude(),
            currentLocation.getLongitude()
        );

        // Validate location
        GlobalScope.INSTANCE.launch(Dispatchers.getMain(), (scope, continuation) -> {
            GeofenceFormHelper.ValidationResult result =
                GeofenceFormHelper.INSTANCE.validateLocationForUser(this, mapPoint);

            if (result.isValid() || locationOverridden) {
                // Proceed with submission
                proceedWithFinalization();
            } else {
                // Show validation dialog
                boolean canOverride =
                    GeofenceFormHelper.INSTANCE.canOverrideLocationRestrictions(this);
                LocationValidationDialogFragment dialog =
                    LocationValidationDialogFragment.Companion.newInstance(result, canOverride);
                dialog.show(getSupportFragmentManager(), "location_validation");
            }
            return null;
        });
    }

    @Override
    public void onOverrideLocation() {
        // Admin override - log and proceed
        logOverrideEvent();
        locationOverridden = true;
        proceedWithFinalization();
    }

    @Override
    public void onCancelForm() {
        // User cancelled - stay on form
        showToast("Form submission cancelled");
    }

    private void logOverrideEvent() {
        // TODO: Add override logging
        Timber.i("Location validation overridden by admin");
    }
}
```

---

### Integration Point 3: Field Detection & Locking

**When:** Form fields are rendered
**Where:** Widget creation/display
**What:** Detect location fields and lock them based on role

**Code Snippet:**

```java
private void setupFormField(FormEntryPrompt prompt) {
    String fieldName = getFieldName(prompt);

    // Check if this is a location-related field
    if (isLocationField(fieldName)) {
        // Lock field for state users
        UserRole userRole = LoginActivity.getUserRole(this);
        if (userRole.isStateLevel()) {
            prompt.setReadOnly(true);
            prompt.setHint("Auto-populated based on GPS location");
        }
    }
}

private boolean isLocationField(String fieldName) {
    String lowerName = fieldName.toLowerCase();
    return lowerName.contains("state") ||
           lowerName.contains("lga") ||
           lowerName.contains("catchment") ||
           lowerName.contains("intervention");
}
```

---

## 🔧 Simplified Integration (Minimal Approach)

If full integration is complex, start with **manual validation** on form save:

### Step 1: Add validation button/check

```java
// In FormFillingActivity
private Button validateLocationButton;

@Override
protected void onCreate(Bundle savedInstanceState) {
    // ... existing code

    validateLocationButton = findViewById(R.id.validate_location_btn);
    validateLocationButton.setOnClickListener(v -> validateCurrentLocation());
}

private void validateCurrentLocation() {
    if (currentLocation == null) {
        showToast("No GPS location available");
        return;
    }

    MapPoint point = new MapPoint(
        currentLocation.getLatitude(),
        currentLocation.getLongitude()
    );

    GlobalScope.INSTANCE.launch(Dispatchers.getMain(), (scope, continuation) -> {
        // Auto-populate fields
        GeofenceFormHelper.LocationFieldValues fieldValues =
            GeofenceFormHelper.INSTANCE.autoPopulateLocationFields(this, point);

        if (fieldValues.isWithinBoundaries()) {
            showLocationInfo(fieldValues);
        } else {
            showToast("Location validation failed: " + fieldValues.getErrorMessage());
        }
        return null;
    });
}

private void showLocationInfo(GeofenceFormHelper.LocationFieldValues fieldValues) {
    String message = "Location validated:\n" +
                     "State: " + fieldValues.getState() + "\n" +
                     "LGA: " + fieldValues.getLga() + "\n" +
                     "Strategic Catchment: " + fieldValues.getStrategicCatchment();

    new AlertDialog.Builder(this)
        .setTitle("Location Information")
        .setMessage(message)
        .setPositiveButton("Auto-fill Fields", (dialog, which) -> {
            autoFillLocationFields(fieldValues);
        })
        .setNegativeButton("Cancel", null)
        .show();
}
```

---

## 🧪 Testing Strategy

### Phase 1: Manual Testing

1. **Create Test Form:**
   ```xml
   <!-- test_geofence_form.xml -->
   <h:html xmlns="http://www.w3.org/2002/xforms"
           xmlns:h="http://www.w3.org/1999/xhtml">
     <h:head>
       <h:title>Geofence Test Form</h:title>
       <model>
         <instance>
           <data id="geofence_test">
             <state/>
             <lga/>
             <strategic_catchment/>
             <micro_catchment/>
             <notes/>
           </data>
         </instance>
       </model>
     </h:head>
     <h:body>
       <input ref="state">
         <label>State</label>
       </input>
       <input ref="lga">
         <label>LGA</label>
       </input>
       <input ref="strategic_catchment">
         <label>Strategic Catchment</label>
       </input>
       <input ref="micro_catchment">
         <label>Micro Catchment</label>
       </input>
       <input ref="notes">
         <label>Notes</label>
       </input>
     </h:body>
   </h:html>
   ```

2. **Test Scenarios:**

   | Scenario | User Role | Location | Expected Result |
   |----------|-----------|----------|-----------------|
   | 1 | State User (Kaduna) | Kaduna | ✅ Auto-populate, allow submit |
   | 2 | State User (Kaduna) | Kano | ❌ Show validation error |
   | 3 | Federal Admin | Anywhere | ✅ Allow submit |
   | 4 | Federal Admin | Lagos (outside) | ⚠️ Override button shown |
   | 5 | State User (Kaduna) | Lagos | ❌ Validation error, no override |

3. **Validation Checklist:**
   - [ ] Fields auto-populate when GPS acquires location
   - [ ] State user blocked from submitting in wrong state
   - [ ] Federal admin sees override button
   - [ ] Override logs event properly
   - [ ] Location outside boundaries shows appropriate error

---

## 📝 Implementation Checklist

### Phase 3A: Basic Integration (Current Sprint)
- [x] Create GeofenceFormHelper
- [x] Create LocationValidationDialogFragment
- [x] Create unit tests
- [ ] **Find form finalization method**
- [ ] **Add validation call before finalization**
- [ ] **Test with real form on device**

### Phase 3B: Advanced Integration (Future)
- [ ] Auto-detect location fields in form
- [ ] Auto-populate on GPS fix
- [ ] Lock fields based on user role
- [ ] Add override logging to database
- [ ] Create override justification field

---

## 🚀 Quick Start (For You)

### Option A: Full Integration
1. Open `FormFillingActivity.java`
2. Find the `finalize` or `save` method
3. Add validation check before proceeding
4. Show `LocationValidationDialogFragment` if invalid

### Option B: Add Validation Button
1. Add button to form toolbar: "Validate Location"
2. On click: call `GeofenceFormHelper.autoPopulateLocationFields()`
3. Show results in dialog
4. Let user manually fill or auto-fill

### Option C: Test in Isolation
1. Create standalone activity to test geofencing
2. Mock form submission
3. Verify validation logic works
4. Then integrate with real forms

---

## 💡 Recommended Approach

**Start Simple → Add Complexity:**

1. **Week 1:** Add validation button to test manually ✅ Easy
2. **Week 2:** Hook into form save to validate automatically
3. **Week 3:** Add auto-population on GPS fix
4. **Week 4:** Add field detection and locking

This allows incremental testing and reduces risk of breaking existing functionality.

---

## 🔍 Where to Look Next

**Key Files to Examine:**
- `FormFillingActivity.java` - Main form activity
- `FormEntryViewModel.java` - Form state management
- `FormController.java` - Form logic controller
- Widget classes - Individual field widgets

**Search Terms:**
- `finalize` - Form finalization
- `saveForm` - Form saving
- `submitForm` - Form submission
- `getAnswer` / `setAnswer` - Field value access

---

## ❓ Need Help?

**If you can identify:**
1. Where forms are finalized/saved
2. Where GPS location is obtained
3. How to set field values programmatically

**Then I can provide:**
- Exact code to add
- Integration points
- Testing approach

---

**Status:** ✅ **Helper Ready** | ⏳ **Integration Pending**

**Next Step:** Find form save/finalize method and add validation call

---

**END OF INTEGRATION GUIDE**
