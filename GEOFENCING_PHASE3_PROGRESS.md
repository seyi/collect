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

### 1. GeofenceFormHelper.kt (189 lines) ✅

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

### 4. FormFillingActivity.java Integration ✅

**Purpose:** Form activity callback implementation

**Features:**
- ✅ Implements `LocationValidationCallback` interface
- ✅ `onOverrideLocation()` - Handles admin override action
- ✅ `onCancelForm()` - Exits form when user cancels due to validation failure
- ✅ Logs all override and cancellation events

**Code Added:**
```java
@Override
public void onOverrideLocation() {
    Timber.i("Location restriction overridden by admin user");
    // Allow form to continue normally
}

@Override
public void onCancelForm() {
    Timber.i("Form cancelled due to location validation");
    finish();
}
```

**Location:** `collect_app/src/main/java/org/odk/collect/android/activities/FormFillingActivity.java:2186-2200`

---

### 5. MainMenuFragment.kt - Real-time Geofence Status ✅

**Purpose:** Display current location's geofence status in main menu

**Features:**
- ✅ Loads all 20 Nigerian state geofences on startup
- ✅ Real-time GPS location tracking
- ✅ Displays current state and strategic catchment
- ✅ Updates every 10 seconds or 10 meters movement
- ✅ Comprehensive logging for debugging

**Key Implementation:**
```kotlin
private fun startLocationTracking() {
    // Load all 20 state geofences
    lifecycleScope.launch {
        val states = listOf("Adamawa", "Bauchi", "Benue", "Borno", "Fct",
                           "Gombe", "Jigawa", "Kaduna", "Kano", "Katsina",
                           "Kebbi", "Kogi", "Kwara", "Nasarawa", "Niger",
                           "Plateau", "Sokoto", "Taraba", "Yobe", "Zamfara")

        states.forEach { state ->
            geoFenceManager.loadGeofences(state)
        }
    }

    // Start GPS tracking
    locationManager?.requestLocationUpdates(
        LocationManager.GPS_PROVIDER, 10000L, 10f, this
    )
}

private fun updateGeofenceStatus() {
    val polygons = geoFenceManager.getContainingPolygons(point)

    if (polygons.isEmpty()) {
        textView.text = "Outside"
    } else {
        val state = polygons.find { it.type == GeofenceType.STATE }
        val catchment = polygons.find { it.type == GeofenceType.STRATEGIC_CATCHMENT }
        textView.text = "${state?.name} | ${catchment?.name}"
    }
}
```

**Display Format:**
- Outside boundaries: `"Outside"`
- Inside boundaries: `"Kano | Hadejia"`
- Loading: `"Loading geofences..."`

**Location:** `collect_app/src/main/java/org/odk/collect/android/mainmenu/MainMenuFragment.kt:324-467`

---

### 6. BaseLocationClient.kt - FakeGPS Support ✅

**Purpose:** Fix GPS location acquisition with FakeGPS apps

**Problem Solved:**
Form location questions got stuck on "Getting Location" dialog when using FakeGPS apps because many mock location apps don't properly enable GPS_PROVIDER.

**Features:**
- ✅ Provider fallback mechanism
- ✅ Detailed logging for provider selection
- ✅ Uses ANY available provider if preferred ones disabled
- ✅ Compatible with FakeGPS, GPS Emulator, and other mock location apps

**Key Implementation:**
```kotlin
private fun getProviderIfEnabled(provider: String, backupProvider: String?): String? {
    if (hasProvider(provider)) return provider
    if (hasProvider(backupProvider)) return backupProvider

    // Fallback for FakeGPS compatibility
    val allProviders = locationManager?.getAllProviders() ?: emptyList()

    // Try GPS even if "disabled" (works with FakeGPS)
    if (LocationManager.GPS_PROVIDER in allProviders) {
        return LocationManager.GPS_PROVIDER
    }

    // Then network, then passive
    return allProviders.firstOrNull()
}
```

**Debug Logging:**
```
GPS Provider selection - Requested: gps, Backup: network, Selected: gps
Checking provider 'gps' - Enabled providers: network, passive
Provider 'gps' is NOT enabled
Using GPS_PROVIDER as fallback (may work with FakeGPS)
```

**Location:** `location/src/main/java/org/odk/collect/location/BaseLocationClient.kt:54-84`

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
| GeofenceFormHelper | ✅ Complete | 189 | 8/8 |
| LocationValidationDialog | ✅ Complete | 90 | Manual |
| FormFillingActivity Callbacks | ✅ Complete | 14 | Manual |
| MainMenu Geofence Display | ✅ Complete | 143 | Manual |
| FakeGPS Support | ✅ Complete | 30 | Manual |
| Form Field Detection | ⏳ Pending | - | - |
| Pre-Submission Validation | ⏳ Pending | - | - |
| Override Logging | ⏳ Pending | - | - |

**Total Progress:** ~60% complete

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

### Manual Testing:
- [x] **Real-time geofence status display** - Main menu shows current location
- [x] **FakeGPS compatibility** - Location questions work with FakeGPS apps
- [x] **Geofence loading** - All 20 states load successfully on app startup
- [x] **GPS provider fallback** - Works when GPS disabled but FakeGPS active
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

**Phase 3 Status:** 🔄 **60% Complete** - Core Components Ready, Real-time Validation Active

**Completed:**
- ✅ GeofenceFormHelper with validation logic
- ✅ LocationValidationDialog for error display
- ✅ FormFillingActivity callback integration
- ✅ Main menu real-time geofence status
- ✅ FakeGPS compatibility for testing
- ✅ All 20 states auto-load on startup

**Next Milestone:** Form field auto-population and pre-submission validation

---

## 🔧 Recent Changes (Latest Commit)

### October 8, 2025 - FakeGPS Support & Real-time Validation

**Files Modified:**
1. `FormFillingActivity.java` - Added LocationValidationCallback methods
2. `MainMenuFragment.kt` - Added geofence loading and real-time status display
3. `BaseLocationClient.kt` - Added provider fallback for FakeGPS compatibility

**Key Features Added:**
- Real-time location validation in main menu
- Automatic loading of all 20 state geofences
- FakeGPS app compatibility with provider fallback
- Comprehensive debug logging for troubleshooting

**Bug Fixes:**
- Fixed "Getting Location" dialog stuck with FakeGPS apps
- Fixed "Outside" showing when inside boundaries (geofences not loaded)

---

**END OF PHASE 3 PROGRESS DOCUMENT**
