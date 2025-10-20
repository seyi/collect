# Development Location Dialog

**Purpose:** Display GPS coordinates and detected geofence boundaries for development/testing
**Location:** Triggered from "Enter Data" button in Main Menu
**Status:** ✅ DEVELOPMENT ONLY - Remove before production

---

## How It Works

### Trigger Point

**Button:** Main Menu → "Enter Data" button

**Code Location:** `MainMenuFragment.kt:219-228`

```kotlin
binding.enterData.setOnClickListener {
    ActionRegister.actionDetected()

    // DEVELOPMENT: Show location dialog before opening form
    showDevelopmentLocationDialog()  // <-- SHOWS DIALOG HERE

    formEntryFlowLauncher.launch(
        Intent(requireActivity(), BlankFormListActivity::class.java)
    )
}
```

---

## Dialog Display

### When GPS is Available

```
┌────────────────────────────────────────────────────┐
│   🌍 Geofence Location Info                        │
├────────────────────────────────────────────────────┤
│                                                    │
│  📍 GPS Coordinates:                               │
│  Latitude: 10.510500                              │
│  Longitude: 7.416500                              │
│  Accuracy: 15.0 meters                            │
│                                                    │
│  🗺️ Detected Boundaries:                          │
│                                                    │
│  State:                                           │
│    Kaduna                                         │
│                                                    │
│  LGA:                                             │
│    Kaduna North                                   │
│                                                    │
│  Strategic Catchment:                             │
│    Hadejia                                        │
│                                                    │
│  Micro Catchment:                                 │
│    MC-Hadejia-001                                 │
│                                                    │
│  Intervention Site:                               │
│    INT-KD-045                                     │
│                                                    │
│  📊 Total polygons found: 4                       │
│                                                    │
│  [ Copy Coordinates ]            [ OK ]           │
│                                                    │
└────────────────────────────────────────────────────┘
```

### When GPS is NOT Available

```
┌────────────────────────────────────────────────────┐
│   ⚠️ No GPS Location                              │
├────────────────────────────────────────────────────┤
│                                                    │
│  GPS location is not available.                   │
│                                                    │
│  Please ensure:                                   │
│  • Location services are enabled                  │
│  • App has location permission                    │
│  • You are outdoors with clear sky view           │
│                                                    │
│                    [ OK ]                         │
│                                                    │
└────────────────────────────────────────────────────┘
```

### When Outside All Boundaries

```
┌────────────────────────────────────────────────────┐
│   🌍 Geofence Location Info                        │
├────────────────────────────────────────────────────┤
│                                                    │
│  📍 GPS Coordinates:                               │
│  Latitude: 0.000000                               │
│  Longitude: 0.000000                              │
│  Accuracy: 20.0 meters                            │
│                                                    │
│  🗺️ Detected Boundaries:                          │
│                                                    │
│  ❌ Location is outside all mapped boundaries     │
│                                                    │
│  Error: Location is outside all mapped boundaries │
│                                                    │
│  📊 Total polygons found: 0                       │
│                                                    │
│  [ Copy Coordinates ]            [ OK ]           │
│                                                    │
└────────────────────────────────────────────────────┘
```

---

## Features

### 1. GPS Coordinates Display
- **Latitude:** 6 decimal places (e.g., 10.510500)
- **Longitude:** 6 decimal places (e.g., 7.416500)
- **Accuracy:** Meters with 1 decimal place (e.g., 15.0 meters)

### 2. Detected Boundaries
Shows all detected geofence layers:
- **State:** Top-level administrative boundary
- **LGA:** Local Government Area
- **Strategic Catchment:** Major water catchment area
- **Micro Catchment:** Smaller sub-catchment area
- **Intervention Site:** Specific project intervention location

Each field shows:
- ✅ Detected value (e.g., "Kaduna")
- ❌ "Not detected" if not found

### 3. Copy Coordinates Button
- Copies coordinates to clipboard in format: `10.510500, 7.416500`
- Shows confirmation toast
- Useful for:
  - Testing with specific coordinates
  - Documentation
  - Sharing with team
  - Creating test data

### 4. Total Polygons Count
- Shows how many overlapping polygons were found
- Helps debug geofence detection
- Example: `Total polygons found: 4`

---

## Usage Flow

```
User Flow:
1. User opens app and logs in
2. User goes to Main Menu
3. User clicks "Enter Data" button
   ↓
   Dialog appears showing location info
   ↓
4. User reviews GPS coordinates and boundaries
5. User clicks "OK" to continue
   ↓
   Form list opens normally
```

**Key Point:** Dialog is **non-blocking** - form list opens after user clicks OK

---

## Testing Scenarios

### Test 1: Location Detected Successfully

**Setup:**
- Enable GPS
- Ensure location permission granted
- Stand in Kaduna city

**Steps:**
1. Go to Main Menu
2. Click "Enter Data"

**Expected Result:**
```
✅ GPS Coordinates: 10.510500, 7.416500
✅ State: Kaduna
✅ LGA: Kaduna North
✅ Strategic Catchment: Hadejia
✅ Micro Catchment: MC-001
📊 Total polygons: 3-5
```

---

### Test 2: No GPS Available

**Setup:**
- Disable Location Services
OR
- Deny location permission
OR
- Test indoors

**Steps:**
1. Go to Main Menu
2. Click "Enter Data"

**Expected Result:**
```
⚠️ No GPS Location

GPS location is not available.

Please ensure:
• Location services are enabled
• App has location permission
• You are outdoors with clear sky view
```

---

### Test 3: Outside All Boundaries

**Setup:**
- Mock GPS to coordinates outside Nigeria (e.g., 0.0, 0.0)
- Use Fake GPS app

**Steps:**
1. Go to Main Menu
2. Click "Enter Data"

**Expected Result:**
```
📍 GPS Coordinates: 0.000000, 0.000000
❌ Location is outside all mapped boundaries
📊 Total polygons: 0
```

---

### Test 4: Copy Coordinates

**Setup:**
- GPS available

**Steps:**
1. Go to Main Menu
2. Click "Enter Data"
3. Dialog appears
4. Click "Copy Coordinates"

**Expected Result:**
- Toast: "Coordinates copied: 10.510500, 7.416500"
- Clipboard contains: `10.510500, 7.416500`
- Can paste coordinates anywhere

---

## Removing for Production

### Method 1: Comment Out (Recommended)

**File:** `MainMenuFragment.kt:222-223`

```kotlin
binding.enterData.setOnClickListener {
    ActionRegister.actionDetected()

    // DEVELOPMENT: Show location dialog before opening form
    // showDevelopmentLocationDialog()  // <-- COMMENTED OUT

    formEntryFlowLauncher.launch(
        Intent(requireActivity(), BlankFormListActivity::class.java)
    )
}
```

---

### Method 2: Use Build Config (Better)

**File:** `MainMenuFragment.kt:222-223`

```kotlin
binding.enterData.setOnClickListener {
    ActionRegister.actionDetected()

    // DEVELOPMENT: Only show in debug builds
    if (BuildConfig.DEBUG) {
        showDevelopmentLocationDialog()
    }

    formEntryFlowLauncher.launch(
        Intent(requireActivity(), BlankFormListActivity::class.java)
    )
}
```

This way:
- ✅ Dialog shows in **debug** builds (for testing)
- ❌ Dialog does NOT show in **release** builds (production)
- No need to manually comment/uncomment

---

### Method 3: Delete Method (Not Recommended)

You can also delete the entire `showDevelopmentLocationDialog()` method, but this makes it harder to re-add for future testing.

---

## Code Locations

### Main Implementation

**File:** `MainMenuFragment.kt`

**Trigger Location:** Lines 219-228
```kotlin
binding.enterData.setOnClickListener {
    // ...
    showDevelopmentLocationDialog()
    // ...
}
```

**Method Implementation:** Lines 510-605
```kotlin
private fun showDevelopmentLocationDialog() {
    // ... full implementation ...
}
```

---

### Alternative Implementation (FormFillingActivity)

There's also a dialog method in `FormFillingActivity.java` (lines 2492-2576) that can show location info during form filling. It's currently **commented out** but can be enabled if needed.

**To enable form-level dialog:**

Uncomment line 2477 in `FormFillingActivity.java`:
```java
// DEVELOPMENT: Uncomment below to show dialog on form field auto-populate
showDevelopmentLocationDialog(currentLocation, fieldValues);  // <-- UNCOMMENT THIS
```

---

## Benefits

### For Development

✅ **Instant Feedback**
- See GPS coordinates immediately
- Verify location detection is working
- No need to check Logcat

✅ **Easy Testing**
- Copy coordinates for test data
- Verify boundaries are correct
- Test different locations quickly

✅ **Debugging**
- See exactly what was detected
- Identify missing boundaries
- Confirm polygon count

### For Field Testing

✅ **Documentation**
- Record actual coordinates visited
- Verify site locations
- Create test cases from real data

✅ **Validation**
- Confirm state boundaries
- Check catchment detection
- Verify intervention sites

---

## Logs

**Logcat output when dialog is shown:**

```bash
I/MainMenuFragment: Development location dialog shown: lat=10.510500, lon=7.416500
```

**To filter logs:**
```bash
adb logcat -s "MainMenuFragment" "GeofenceFormHelper" "GeoFenceManager"
```

---

## Known Issues

### Issue 1: Dialog Appears Every Click

**Symptom:** Dialog shows every time "Enter Data" is clicked

**Solution:** This is by design for development. To disable, comment out the call.

---

### Issue 2: Slow Response

**Symptom:** Dialog takes 2-3 seconds to appear

**Cause:** Querying geofences in background thread

**Solution:** This is normal. The delay ensures UI remains responsive.

---

### Issue 3: "Not detected" for all boundaries

**Symptom:** All boundaries show "Not detected" even though GPS is working

**Possible Causes:**
1. Location is actually outside mapped areas
2. Geofences not loaded yet (check cache stats)
3. State data not loaded for user

**Solution:**
- Check toolbar status (should show state name if loaded)
- Try a known location (Kaduna: 10.5105, 7.4165)
- Check Logcat for loading errors

---

## Summary

The development location dialog provides **instant visual feedback** when clicking "Enter Data":

📍 **Shows:**
- GPS coordinates (lat, lon, accuracy)
- All detected boundaries (state, LGA, catchments)
- Total polygon count
- Copy coordinates button

🎯 **Purpose:**
- Verify geofence detection
- Test different locations
- Debug boundary issues
- Document field coordinates

🚀 **Usage:**
- Click "Enter Data" → Dialog shows → Click OK → Form list opens

⚠️ **Production:**
- Comment out or wrap with `if (BuildConfig.DEBUG)`
- See "Removing for Production" section above

---

**Last Updated:** October 2025
**Status:** ✅ IMPLEMENTED (Development Only)
**TODO:** Remove or disable before production deployment
