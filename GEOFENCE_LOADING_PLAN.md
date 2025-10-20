# Geofence Loading Plan - State-Specific Data Loading

**Goal:** Ensure geofence data (State, LGA, Strategic Catchment, Micro Catchment) is loaded ONLY for the user's assigned state

**Date:** October 2025
**Status:** 🎯 IMPLEMENTATION PLAN

---

## Current State Analysis

### What Works ✅
- `GeoFenceManager` has singleton pattern for managing geofence data
- `LoginActivity` already has `loadGeofencesForUser()` method (lines 247-298)
- State-level users are identified correctly via `UserRole`
- User state assignment is stored in SharedPreferences
- Cache mechanism exists for storing loaded polygons

### Issues Found ❌

1. **Missing Boundary Types**
   - Current: Only loads STRATEGIC_CATCHMENT, MICRO_CATCHMENT, INTERVENTION
   - Missing: STATE and LGA boundary types
   - Location: `GeoFenceManager.kt` line 61-65

2. **Timing Problem**
   - Geofences load AFTER navigation begins
   - No blocking/waiting for load to complete
   - User can access forms before geofences are ready
   - Location: `LoginActivity.kt` lines 244, 205

3. **No User Feedback**
   - No loading indicator shown during geofence load
   - User has no idea data is loading
   - No error message if load fails

4. **Federal Users Load All States**
   - Federal users currently load ALL 20 states
   - This is inefficient and may not be necessary
   - Should load states on-demand instead

---

## Implementation Plan

### Phase 1: Add STATE and LGA Support to GeoFenceManager

#### 1.1 Update GeoFenceManager Constants

**File:** `GeoFenceManager.kt` (lines 34-37)

**Current:**
```kotlin
private const val STRATEGIC_CATCHMENTS_SUFFIX = "_strategic_catchments.geojson"
private const val MICRO_CATCHMENTS_SUFFIX = "_micro_catchments.geojson"
private const val INTERVENTIONS_SUFFIX = "_interventions.geojson"
```

**Add:**
```kotlin
private const val STATE_BOUNDARY_SUFFIX = "_state_boundary.geojson"
private const val LGA_BOUNDARIES_SUFFIX = "_lga_boundaries.geojson"
private const val STRATEGIC_CATCHMENTS_SUFFIX = "_strategic_catchments.geojson"
private const val MICRO_CATCHMENTS_SUFFIX = "_micro_catchments.geojson"
private const val INTERVENTIONS_SUFFIX = "_interventions.geojson"
```

#### 1.2 Update Default Load Types

**File:** `GeoFenceManager.kt` (lines 59-66)

**Current:**
```kotlin
suspend fun loadGeofences(
    state: String? = null,
    types: List<GeofenceType> = listOf(
        GeofenceType.STRATEGIC_CATCHMENT,
        GeofenceType.MICRO_CATCHMENT,
        GeofenceType.INTERVENTION
    )
): Boolean
```

**Change to:**
```kotlin
suspend fun loadGeofences(
    state: String? = null,
    types: List<GeofenceType> = listOf(
        GeofenceType.STATE,
        GeofenceType.LGA,
        GeofenceType.STRATEGIC_CATCHMENT,
        GeofenceType.MICRO_CATCHMENT
    )
): Boolean
```

**Note:** Removed INTERVENTION since we excluded it

#### 1.3 Update File Loading Logic

**File:** `GeoFenceManager.kt` (lines 115-123)

**Current:**
```kotlin
val fileName = when (type) {
    GeofenceType.STRATEGIC_CATCHMENT -> "$state$STRATEGIC_CATCHMENTS_SUFFIX"
    GeofenceType.MICRO_CATCHMENT -> "$state$MICRO_CATCHMENTS_SUFFIX"
    GeofenceType.INTERVENTION -> "$state$INTERVENTIONS_SUFFIX"
    else -> {
        Timber.w("Unsupported type for asset loading: $type")
        return emptyList()
    }
}
```

**Change to:**
```kotlin
val fileName = when (type) {
    GeofenceType.STATE -> "$state$STATE_BOUNDARY_SUFFIX"
    GeofenceType.LGA -> "$state$LGA_BOUNDARIES_SUFFIX"
    GeofenceType.STRATEGIC_CATCHMENT -> "$state$STRATEGIC_CATCHMENTS_SUFFIX"
    GeofenceType.MICRO_CATCHMENT -> "$state$MICRO_CATCHMENTS_SUFFIX"
    else -> {
        Timber.w("Unsupported type for asset loading: $type")
        return emptyList()
    }
}
```

---

### Phase 2: Implement Loading Dialog in LoginActivity

#### 2.1 Add Progress Dialog

**File:** `LoginActivity.kt` (after line 298)

**Add new method:**
```kotlin
private fun showGeofenceLoadingDialog(state: String): androidx.appcompat.app.AlertDialog {
    val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
        .setTitle("Loading Geofence Data")
        .setMessage("Loading boundary data for $state...\n\nPlease wait.")
        .setCancelable(false)
        .create()

    dialog.show()
    return dialog
}
```

#### 2.2 Update `saveUserSession()` Method

**File:** `LoginActivity.kt` (line 222-245)

**Current:**
```kotlin
private fun saveUserSession(userData: UserData) {
    val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    // ... save session data ...

    // Load geofences for the user's state after successful login
    loadGeofencesForUser(userData)  // <-- RUNS IN BACKGROUND
}
```

**Change to:**
```kotlin
private fun saveUserSession(userData: UserData) {
    val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    // ... save session data ...

    // Load geofences BEFORE navigation (blocking)
    // Navigation will happen in loadGeofencesForUser() after loading completes
}
```

#### 2.3 Update `loadGeofencesForUser()` Method

**File:** `LoginActivity.kt` (lines 247-298)

**Changes needed:**
1. Show loading dialog
2. Load geofences synchronously (blocking)
3. Hide dialog when complete
4. Call `navigateToApp()` AFTER geofences load
5. Handle errors gracefully

**New implementation:**
```kotlin
private fun loadGeofencesForUser(userData: UserData) {
    lifecycleScope.launch {
        var loadingDialog: androidx.appcompat.app.AlertDialog? = null

        try {
            Timber.d("Loading geofences for user: ${userData.username}, state: ${userData.state}")

            val geoFenceManager = GeoFenceManager.getInstance(this@LoginActivity)

            // Determine which state(s) to load based on user role
            val statesToLoad = when (userData.role) {
                UserRole.STATE_ADMIN, UserRole.STATE_USER -> {
                    // Load only user's assigned state
                    if (userData.state.isNotEmpty()) {
                        Timber.d("State user detected, loading state: ${userData.state}")

                        // Show loading dialog
                        loadingDialog = showGeofenceLoadingDialog(userData.state)

                        userData.state
                    } else {
                        Timber.w("State user has no state assigned, skipping geofence loading")
                        navigateToApp()
                        return@launch
                    }
                }
                UserRole.FEDERAL_ADMIN, UserRole.FEDERAL_USER, UserRole.ADMIN, UserRole.TEST_USER -> {
                    // Federal users don't need to preload all states
                    // They can load on-demand when needed
                    Timber.d("Federal/admin user detected, skipping preload")
                    navigateToApp()
                    return@launch
                }
                UserRole.UNKNOWN -> {
                    Timber.w("Unknown user role, skipping geofence loading")
                    navigateToApp()
                    return@launch
                }
            }

            // Load geofences synchronously (blocking UI)
            val success = geoFenceManager.loadGeofences(statesToLoad)

            // Hide loading dialog
            loadingDialog?.dismiss()

            if (success) {
                val stats = geoFenceManager.getCacheStats()
                Timber.d("Geofences loaded successfully: ${stats.totalPolygons} polygons, " +
                        "${stats.loadedStates.size} states")
                ToastUtils.showShortToast(this@LoginActivity,
                    "Loaded ${stats.totalPolygons} boundaries for ${userData.state}")
            } else {
                Timber.w("Failed to load geofences")
                ToastUtils.showShortToast(this@LoginActivity,
                    "Warning: Failed to load boundary data")
            }

            // Navigate to app AFTER geofences are loaded
            navigateToApp()

        } catch (e: Exception) {
            Timber.e(e, "Error loading geofences: ${e.message}")

            // Hide loading dialog on error
            loadingDialog?.dismiss()

            // Show error but allow navigation
            ToastUtils.showShortToast(this@LoginActivity,
                "Error loading boundaries: ${e.message}")

            navigateToApp()
        }
    }
}
```

#### 2.4 Update Authentication Success Flow

**File:** `LoginActivity.kt` (lines 189-211)

**Current:**
```kotlin
when (result) {
    is AuthResult.Success -> {
        setLoadingState(false)
        saveUserSession(result.userData)
        ToastUtils.showShortToast(this@LoginActivity, "Login successful!")
        navigateToApp()  // <-- CALLED HERE
    }
    // ...
}
```

**Change to:**
```kotlin
when (result) {
    is AuthResult.Success -> {
        setLoadingState(false)
        saveUserSession(result.userData)
        ToastUtils.showShortToast(this@LoginActivity, "Login successful!")

        // Load geofences BEFORE navigating
        loadGeofencesForUser(result.userData)  // <-- Calls navigateToApp() internally
    }
    // ...
}
```

**Same for fallback auth** (lines 203-205):
```kotlin
saveUserSession(fallbackUserData)
ToastUtils.showShortToast(this@LoginActivity, "Logged in offline mode")
loadGeofencesForUser(fallbackUserData)  // <-- Changed from navigateToApp()
```

---

### Phase 3: Add Geofence Check in MainMenuFragment

#### 3.1 Update Development Dialog

**File:** `MainMenuFragment.kt` (lines 516-622)

**Add check at start of `showDevelopmentLocationDialog()`:**

```kotlin
private fun showDevelopmentLocationDialog(onDismiss: () -> Unit) {
    // Check if geofences are loaded
    val geoFenceManager = GeoFenceManager.getInstance(requireContext())
    val userState = LoginActivity.getUserState(requireContext())

    if (!geoFenceManager.isStateLoaded(userState)) {
        // Geofences not loaded yet - show warning
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Geofence Data Not Loaded")
            .setMessage("Boundary data for $userState is not loaded yet.\n\nPlease wait a moment and try again.")
            .setPositiveButton("OK") { _, _ -> onDismiss() }
            .setCancelable(false)
            .show()
        return
    }

    // Continue with existing logic...
    val location = currentLocation
    // ...
}
```

---

### Phase 4: Asset File Structure

#### 4.1 Required GeoJSON Files

For each state, the following files should exist in `assets/geofencing/{STATE}/`:

```
assets/
└── geofencing/
    ├── Kaduna/
    │   ├── Kaduna_state_boundary.geojson       (NEW - State boundary)
    │   ├── Kaduna_lga_boundaries.geojson       (NEW - All LGAs in Kaduna)
    │   ├── Kaduna_strategic_catchments.geojson (Existing)
    │   └── Kaduna_micro_catchments.geojson     (Existing)
    ├── Kano/
    │   ├── Kano_state_boundary.geojson
    │   ├── Kano_lga_boundaries.geojson
    │   ├── Kano_strategic_catchments.geojson
    │   └── Kano_micro_catchments.geojson
    └── [... other 18 states ...]
```

#### 4.2 State Boundary GeoJSON Format

**File:** `Kaduna_state_boundary.geojson`

```json
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "properties": {
        "name": "Kaduna",
        "id": "Kaduna-STATE-1",
        "state": "Kaduna"
      },
      "geometry": {
        "type": "Polygon",
        "coordinates": [
          [
            [7.1234, 10.1234],
            [7.2345, 10.2345],
            // ... more coordinates ...
            [7.1234, 10.1234]
          ]
        ]
      }
    }
  ]
}
```

#### 4.3 LGA Boundaries GeoJSON Format

**File:** `Kaduna_lga_boundaries.geojson`

```json
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "properties": {
        "name": "Kaduna North",
        "id": "Kaduna-LGA-001",
        "state": "Kaduna"
      },
      "geometry": {
        "type": "Polygon",
        "coordinates": [...]
      }
    },
    {
      "type": "Feature",
      "properties": {
        "name": "Kaduna South",
        "id": "Kaduna-LGA-002",
        "state": "Kaduna"
      },
      "geometry": {
        "type": "Polygon",
        "coordinates": [...]
      }
    }
    // ... more LGAs ...
  ]
}
```

---

## Implementation Steps

### Step 1: Update GeoFenceManager ✅
- [ ] Add STATE_BOUNDARY_SUFFIX and LGA_BOUNDARIES_SUFFIX constants
- [ ] Update default load types to include STATE and LGA
- [ ] Update file loading logic to handle STATE and LGA types
- [ ] Remove INTERVENTION type handling

**Estimated Time:** 15 minutes
**Files Modified:** `GeoFenceManager.kt`

### Step 2: Add Loading Dialog ✅
- [ ] Create `showGeofenceLoadingDialog()` method in LoginActivity
- [ ] Update `loadGeofencesForUser()` to show dialog
- [ ] Make loading synchronous (blocking) for state users
- [ ] Show toast with load success/failure

**Estimated Time:** 30 minutes
**Files Modified:** `LoginActivity.kt`

### Step 3: Fix Navigation Timing ✅
- [ ] Remove `navigateToApp()` call from authentication success handler
- [ ] Move `navigateToApp()` call to END of `loadGeofencesForUser()`
- [ ] Ensure geofences load BEFORE navigation

**Estimated Time:** 10 minutes
**Files Modified:** `LoginActivity.kt`

### Step 4: Add Geofence Check in MainMenuFragment ✅
- [ ] Add check at start of `showDevelopmentLocationDialog()`
- [ ] Show warning if state geofences not loaded
- [ ] Prevent location detection when geofences unavailable

**Estimated Time:** 15 minutes
**Files Modified:** `MainMenuFragment.kt`

### Step 5: Prepare Asset Files (External) ⚠️
- [ ] Create STATE boundary GeoJSON files for all 20 states
- [ ] Create LGA boundaries GeoJSON files for all 20 states
- [ ] Place files in correct directory structure
- [ ] Verify file naming matches constants

**Estimated Time:** Variable (depends on data availability)
**Note:** This step may require GIS data processing

### Step 6: Testing ✅
- [ ] Test state user login with valid state assignment
- [ ] Test state user login with no state assignment
- [ ] Test federal user login (should skip preload)
- [ ] Test loading dialog appearance and dismissal
- [ ] Test development dialog with loaded geofences
- [ ] Test development dialog when geofences not loaded
- [ ] Test form validation with loaded state boundaries

**Estimated Time:** 1 hour
**Tools:** Android Studio, Test devices, Mock GPS

### Step 7: Documentation ✅
- [ ] Update GEOFENCING_TESTING_GUIDE.md
- [ ] Update DEVELOPMENT_LOCATION_DIALOG.md
- [ ] Update LOCATION_VALIDATION_WORKFLOW.md
- [ ] Create GEOFENCE_LOADING_WORKFLOW.md

**Estimated Time:** 30 minutes

---

## Expected Behavior After Implementation

### For State Users (STATE_USER, STATE_ADMIN)

**Login Flow:**
```
1. User enters credentials
2. Authentication succeeds
3. "Login successful!" toast
4. Loading dialog appears: "Loading Geofence Data - Loading boundary data for Kaduna... Please wait."
5. GeoFenceManager loads:
   - Kaduna_state_boundary.geojson
   - Kaduna_lga_boundaries.geojson
   - Kaduna_strategic_catchments.geojson
   - Kaduna_micro_catchments.geojson
6. Loading dialog dismisses
7. Toast: "Loaded 45 boundaries for Kaduna" (example)
8. Navigate to Main Menu
```

**Development Dialog (Click "Enter Data"):**
```
📍 GPS Coordinates:
Latitude: 10.510500
Longitude: 7.416500
Accuracy: 15.0 meters

🗺️ Detected Boundaries:

State:
  Kaduna ✅

LGA:
  Kaduna North ✅

Strategic Catchment:
  Hadejia ✅

Micro Catchment:
  MC-Hadejia-001 ✅

📊 Total polygons found: 4
```

### For Federal Users (FEDERAL_USER, FEDERAL_ADMIN, ADMIN)

**Login Flow:**
```
1. User enters credentials
2. Authentication succeeds
3. "Login successful!" toast
4. No loading dialog (geofences loaded on-demand)
5. Navigate to Main Menu immediately
```

**Note:** Federal users can load states on-demand when they visit specific locations

---

## Edge Cases & Error Handling

### Case 1: State Geofence Files Missing

**Scenario:** User assigned to "Zamfara" but `Zamfara_state_boundary.geojson` doesn't exist

**Behavior:**
- Loading will fail for that specific file type
- Other files (strategic catchments, etc.) will still load
- Log warning: `"File not found: geofencing/Zamfara/Zamfara_state_boundary.geojson"`
- User can still proceed but State detection will fail

**Fix:** Ensure all asset files exist before deployment

---

### Case 2: User Has No State Assignment

**Scenario:** State user with empty `state` field in UserData

**Behavior:**
- Skip geofence loading
- Log warning: `"State user has no state assigned, skipping geofence loading"`
- Navigate to app immediately
- Location validation will fail (expected)

**Fix:** Ensure all state users are assigned a state during authentication

---

### Case 3: Network Timeout During Load

**Scenario:** Loading takes too long (assets are large)

**Behavior:**
- Loading dialog shows "Please wait..."
- Load happens on IO dispatcher (non-blocking UI)
- If successful, proceed normally
- If fails, show error toast and navigate anyway

**Mitigation:**
- Optimize GeoJSON file sizes
- Consider compressing files
- Use minimal precision for coordinates

---

### Case 4: App Killed During Load

**Scenario:** User kills app while loading dialog is showing

**Behavior:**
- On next launch, login check will fail
- User returned to LoginActivity
- Must login again
- Geofences will load again

**Fix:** No fix needed - this is expected behavior

---

## Performance Considerations

### Memory Usage

**Per State Estimate:**
- State boundary: ~500 vertices = 20 KB
- LGA boundaries: ~20 LGAs × 300 vertices = 240 KB
- Strategic catchments: ~5 catchments × 500 vertices = 100 KB
- Micro catchments: ~15 catchments × 200 vertices = 120 KB

**Total per state:** ~480 KB

**For 1 state user:** ~480 KB memory usage ✅ Acceptable

**For federal user (all 20 states):** ~9.6 MB memory usage ⚠️ High but manageable

### Load Time Estimate

**Per State:**
- File I/O: ~100ms
- JSON parsing: ~200ms
- Polygon creation: ~100ms

**Total per state:** ~400ms (0.4 seconds) ✅ Acceptable

**For federal user (20 states):** ~8 seconds ⚠️ Too long for preload

**Solution:** Federal users should NOT preload all states. Load on-demand instead.

---

## Testing Checklist

### Unit Tests
- [ ] Test STATE and LGA file loading
- [ ] Test geofence loading for valid state
- [ ] Test geofence loading for invalid state
- [ ] Test cache hit/miss scenarios

### Integration Tests
- [ ] Test login flow with state user
- [ ] Test login flow with federal user
- [ ] Test loading dialog appearance
- [ ] Test navigation timing

### UI Tests
- [ ] Test development dialog with loaded geofences
- [ ] Test development dialog without loaded geofences
- [ ] Test form validation with boundaries

### Manual Testing
- [ ] Login as Kaduna state user → verify Kaduna geofences load
- [ ] Login as Kano state user → verify Kano geofences load
- [ ] Click "Enter Data" → verify all 4 boundary types detected
- [ ] Mock GPS outside state → verify validation fails
- [ ] Mock GPS inside state → verify validation passes

---

## Rollback Plan

If implementation causes issues:

1. **Revert GeoFenceManager changes**
   - Remove STATE and LGA from default load types
   - Remove STATE and LGA file loading logic

2. **Revert LoginActivity changes**
   - Remove loading dialog
   - Call `navigateToApp()` immediately after `saveUserSession()`

3. **Revert MainMenuFragment changes**
   - Remove geofence loaded check

**Rollback time:** ~10 minutes

---

## Success Criteria

✅ **Implementation is successful when:**

1. State users see loading dialog during login
2. State users' assigned state geofences load before navigation
3. Development dialog shows all 4 boundary types (State, LGA, Strategic Catchment, Micro Catchment)
4. Location validation works correctly with State boundary detection
5. Federal users skip preload and navigate immediately
6. No crashes or ANR (Application Not Responding) errors
7. Memory usage stays under 15 MB for single state
8. Load time stays under 1 second for single state

---

## Next Steps After Implementation

1. **Optimize GeoJSON files**
   - Reduce coordinate precision to 6 decimal places
   - Remove unnecessary properties
   - Consider compression

2. **Add on-demand loading for federal users**
   - Load state geofences when entering specific locations
   - Cache loaded states for session

3. **Add cache persistence**
   - Save loaded geofences to local storage
   - Reload from cache on app restart
   - Avoid re-downloading on every login

4. **Add update mechanism**
   - Check for geofence updates on server
   - Download updated boundaries when available

---

**Last Updated:** October 2025
**Status:** 📋 READY FOR IMPLEMENTATION
**Estimated Total Time:** 2-3 hours (excluding asset file preparation)
