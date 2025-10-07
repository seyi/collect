# Geofencing Phase 3 - Form Integration & Validation

**Date:** October 7, 2025
**Status:** 🔄 In Progress

---

## 📦 Phase 3 Objectives

1. **Auto-populate location fields** in forms based on GPS coordinates
2. **Validate user location** against assigned boundaries (state users only)
3. **Admin override mechanism** for federal admins to bypass restrictions
4. **Form field detection** to identify and populate geo-related fields

---

## ✅ Completed Components

### 1. GeofenceFormHelper.kt (180 lines) ✅

**Purpose:** Core utility for form-geofencing integration

**Key Features:**
- ✅ Auto-populate location fields from GPS
- ✅ Validate location against user role
- ✅ Map field names to geofence values
- ✅ Permission checking for overrides

**Main Methods:**

```kotlin
suspend fun autoPopulateLocationFields(
    context: Context,
    location: MapPoint
): LocationFieldValues

suspend fun validateLocationForUser(
    context: Context,
    location: MapPoint
): ValidationResult

fun canOverrideLocationRestrictions(context: Context): Boolean
fun mapFieldValue(fieldName: String, fieldValues: LocationFieldValues): String?
```

**Data Models:**

```kotlin
data class LocationFieldValues(
    val state: String?,
    val lga: String?,
    val strategicCatchment: String?,
    val microCatchment: String?,
    val intervention: String?,
    val isWithinBoundaries: Boolean,
    val errorMessage: String?
)

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String?,
    val requiresOverride: Boolean
)
```

**Location:** `collect_app/src/main/java/org/odk/collect/android/geofencing/GeofenceFormHelper.kt`

---

### 2. GeofenceFormHelperTest.kt (8 tests) ✅

**Purpose:** Unit tests for form helper

**Test Coverage:**
- ✅ Field display name mapping
- ✅ Field value mapping (state, LGA, catchments)
- ✅ Case insensitivity handling
- ✅ Unknown field handling
- ✅ Data model structure validation

**Location:** `collect_app/src/test/java/org/odk/collect/android/geofencing/GeofenceFormHelperTest.kt`

---

### 3. LocationValidationDialogFragment.kt (90 lines) ✅

**Purpose:** Dialog to display validation errors with override option

**Features:**
- ✅ Show validation error message
- ✅ Override button (admins only)
- ✅ Cancel form option
- ✅ Material Design 3 styling
- ✅ Callback interface for actions

**Usage:**
```kotlin
val dialog = LocationValidationDialogFragment.newInstance(
    validationResult = validationResult,
    canOverride = canOverride
)
dialog.show(supportFragmentManager, "location_validation")
```

**Callback Interface:**
```kotlin
interface LocationValidationCallback {
    fun onOverrideLocation()
    fun onCancelForm()
}
```

**Location:** `collect_app/src/main/java/org/odk/collect/android/geofencing/LocationValidationDialogFragment.kt`

---

## 🔄 Integration Points

### How It Works:

```
1. User opens form → GPS location acquired
   ↓
2. GeofenceFormHelper.autoPopulateLocationFields()
   ↓
3. Identifies containing polygons (state, LGA, catchments)
   ↓
4. Maps values to form field names (flexible matching)
   ↓
5. Auto-fills detected location fields
   ↓
6. GeofenceFormHelper.validateLocationForUser()
   ↓
7. Checks user role vs location
   ↓
8. If invalid → Show LocationValidationDialogFragment
   ↓
9. Admin can override OR user must cancel
```

### Validation Rules:

| User Role | Restriction | Can Override |
|-----------|-------------|--------------|
| **Federal Admin** | None | N/A |
| **Federal User** | None | N/A |
| **Admin** | None | N/A |
| **State Admin** | Must be in assigned state | No |
| **State User** | Must be in assigned state | No |
| **Test User** | None (warning only) | N/A |

### Field Name Mapping (Case Insensitive):

| Form Field Name | Maps To |
|-----------------|---------|
| `state`, `state_name` | State polygon name |
| `lga`, `lga_name` | LGA polygon name |
| `strategic_catchment`, `scatchment`, `s_catchment` | Strategic catchment name |
| `micro_catchment`, `mcatchment`, `m_catchment` | Micro catchment name |
| `intervention`, `intervention_site`, `interv_site` | Intervention site name |

---

## 🔨 Next Steps (Pending)

### 4. Form Field Auto-Population Integration ⏳

**Tasks:**
- [ ] Hook into form loading lifecycle
- [ ] Detect location-related fields in XLSForm
- [ ] Call `autoPopulateLocationFields()` on GPS fix
- [ ] Populate detected fields with values
- [ ] Lock fields based on user role

**Target Files:**
- FormEntryActivity or equivalent
- FormController integration

---

### 5. Pre-Submission Validation ⏳

**Tasks:**
- [ ] Add validation check before form submission
- [ ] Call `validateLocationForUser()`
- [ ] Show `LocationValidationDialogFragment` if invalid
- [ ] Log override events for admins
- [ ] Prevent submission if validation fails (non-admins)

**Target Files:**
- Form submission handler
- InstanceUploader integration

---

### 6. Override Event Logging ⏳

**Tasks:**
- [ ] Create override event log system
- [ ] Record: timestamp, user, location, reason
- [ ] Store in local database
- [ ] Include in form metadata
- [ ] Add justification field (optional)

---

## 📊 Current Progress

| Component | Status | Lines of Code | Tests |
|-----------|--------|---------------|-------|
| GeofenceFormHelper | ✅ Complete | 180 | 8/8 |
| LocationValidationDialog | ✅ Complete | 90 | Manual |
| Form Field Detection | ⏳ Pending | - | - |
| Pre-Submission Validation | ⏳ Pending | - | - |
| Override Logging | ⏳ Pending | - | - |

**Total Progress:** ~40% complete

---

## 🧪 Testing Plan

### Unit Tests (Completed):
- ✅ Field name mapping
- ✅ Value extraction
- ✅ Case handling

### Integration Tests (Pending):
- [ ] Test with real XLSForm
- [ ] Test GPS location changes
- [ ] Test state boundary crossing
- [ ] Test admin override flow
- [ ] Test validation rejection

### Manual Testing (Pending):
- [ ] Create test form with location fields
- [ ] Test as state user in correct state
- [ ] Test as state user in wrong state
- [ ] Test as federal user anywhere
- [ ] Test override as admin
- [ ] Verify field auto-population

---

## 📝 Usage Examples

### Example 1: Auto-populate in Activity

```kotlin
class FormActivity : AppCompatActivity() {

    private suspend fun populateLocationFields(location: MapPoint) {
        val fieldValues = GeofenceFormHelper.autoPopulateLocationFields(this, location)

        if (fieldValues.isWithinBoundaries) {
            // Populate form fields
            setFieldValue("state", fieldValues.state)
            setFieldValue("lga", fieldValues.lga)
            setFieldValue("strategic_catchment", fieldValues.strategicCatchment)
        } else {
            showError(fieldValues.errorMessage)
        }
    }
}
```

### Example 2: Validate Before Submission

```kotlin
class FormSubmissionHandler : LocationValidationDialogFragment.LocationValidationCallback {

    private suspend fun validateAndSubmit(location: MapPoint) {
        val result = GeofenceFormHelper.validateLocationForUser(this, location)

        if (!result.isValid) {
            val canOverride = GeofenceFormHelper.canOverrideLocationRestrictions(this)
            val dialog = LocationValidationDialogFragment.newInstance(result, canOverride)
            dialog.show(supportFragmentManager, "validation")
        } else {
            submitForm()
        }
    }

    override fun onOverrideLocation() {
        logOverrideEvent()
        submitForm()
    }

    override fun onCancelForm() {
        finish()
    }
}
```

### Example 3: Generic Field Mapping

```kotlin
// Map any field name to geofence value
val fieldValues = GeofenceFormHelper.autoPopulateLocationFields(context, location)

formFields.forEach { field ->
    val value = GeofenceFormHelper.mapFieldValue(field.name, fieldValues)
    if (value != null) {
        field.setValue(value)
        field.setReadOnly(true) // Lock auto-populated fields
    }
}
```

---

## 🐛 Known Limitations

1. **Field Detection:** Requires form to use standard field names (state, lga, etc.)
2. **GPS Accuracy:** Validation depends on GPS accuracy
3. **Boundary Edge Cases:** Users exactly on boundary may get inconsistent results
4. **Offline:** Requires geofences loaded before form opening

---

## 🔧 Build & Test Instructions

### Build:
```bash
./gradlew :collect_app:compileDebugKotlin
```

### Run Unit Tests:
```bash
./gradlew test --tests "org.odk.collect.android.geofencing.GeofenceFormHelperTest"
```

### Manual Test (After integration):
1. Create XLSForm with fields: `state`, `lga`, `strategic_catchment`
2. Login as state user (e.g., Kaduna state)
3. Open form in Kaduna → Fields auto-populate
4. Travel to different state → Validation error shown
5. Login as federal admin → Override button appears

---

## 📞 API Reference

### GeofenceFormHelper

| Method | Parameters | Returns | Purpose |
|--------|------------|---------|---------|
| `autoPopulateLocationFields()` | `Context`, `MapPoint` | `LocationFieldValues` | Get location values for form |
| `validateLocationForUser()` | `Context`, `MapPoint` | `ValidationResult` | Check if location valid for user |
| `canOverrideLocationRestrictions()` | `Context` | `Boolean` | Check if user can override |
| `getFieldDisplayName()` | `String` | `String` | Get user-friendly field name |
| `mapFieldValue()` | `String`, `LocationFieldValues` | `String?` | Map field name to value |

### LocationValidationDialogFragment

| Method | Parameters | Purpose |
|--------|------------|---------|
| `newInstance()` | `ValidationResult`, `Boolean` | Create dialog instance |
| `LocationValidationCallback.onOverrideLocation()` | - | Called when admin overrides |
| `LocationValidationCallback.onCancelForm()` | - | Called when user cancels |

---

**Phase 3 Status:** 🔄 **40% Complete** - Helper & Dialog Ready, Integration Pending

**Next Milestone:** Integrate with form lifecycle and submission flow

---

**END OF PHASE 3 PROGRESS DOCUMENT**
