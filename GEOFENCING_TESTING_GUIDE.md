# Geofencing Testing Guide

**Project:** ACReSAL Collect App
**Feature:** Location-based Geofencing System
**Date:** October 2025

---

## Overview

This guide provides comprehensive testing procedures to verify that the geofencing system correctly identifies:
- **States** (20 Nigerian states)
- **LGAs** (Local Government Areas)
- **Strategic Catchments**
- **Micro Catchments**
- **Intervention Sites**

---

## Table of Contents

1. [Testing Tools](#testing-tools)
2. [Unit Tests](#unit-tests)
3. [Integration Tests](#integration-tests)
4. [Manual Testing with UI](#manual-testing-with-ui)
5. [Test Coordinates](#test-coordinates)
6. [Expected Results](#expected-results)
7. [Troubleshooting](#troubleshooting)

---

## Testing Tools

### 1. Unit Tests
**File:** `GeofenceIntegrationTest.kt`
**Location:** `collect_app/src/test/java/org/odk/collect/android/geofencing/`

Tests core functionality using real GeoJSON data:
- Point-in-polygon detection
- State boundary detection
- Strategic/micro catchment identification
- Field name mapping
- Performance benchmarks

### 2. Manual Test UI
**File:** `GeofenceTestActivity.kt`
**Location:** `collect_app/src/main/java/org/odk/collect/android/activities/`

Interactive testing tool with:
- Coordinate input fields
- State selection
- Real-time results display
- Sample coordinates
- Load all states feature

---

## Unit Tests

### Running Unit Tests

#### From Android Studio:
1. Open `GeofenceIntegrationTest.kt`
2. Right-click on the file
3. Select "Run 'GeofenceIntegrationTest'"

#### From Command Line:
```bash
./gradlew :collect_app:testDebugUnitTest --tests "*.GeofenceIntegrationTest"
```

### Test Coverage

The unit tests include:

| Test Category | Test Count | Description |
|--------------|------------|-------------|
| **Kaduna Tests** | 2 | State boundary and strategic catchment detection |
| **Kano Tests** | 2 | State and catchment detection |
| **Multi-State Tests** | 1 | Loading multiple states simultaneously |
| **Boundary Tests** | 2 | Edge cases (outside boundaries, wrong state) |
| **Form Helper Tests** | 2 | Auto-populate and field mapping |
| **Performance Tests** | 2 | Query speed and bulk loading |

**Total:** 11 comprehensive tests

### Key Test Cases

#### Test 1: Kaduna Strategic Catchment (Hadejia)
```kotlin
// Coordinate from GeoJSON: [8.775069753616744, 10.519885713067781]
val point = MapPoint(10.519885713067781, 8.775069753616744)
```
**Expected:** Identifies "Hadejia" strategic catchment in Kaduna state

#### Test 2: Kaduna State Boundary
```kotlin
// Central Kaduna city coordinates
val point = MapPoint(10.5105, 7.4165)
```
**Expected:** Identifies Kaduna state

#### Test 3: Performance Benchmark
```kotlin
// 100 queries should average < 50ms per query
```
**Expected:** Query time < 50ms (validates bounding box optimization)

---

## Integration Tests

### Test Scenarios

#### Scenario 1: Auto-Population in Forms
1. **Setup:** Load Kaduna geofences
2. **Action:** Open form with location fields
3. **Expected:**
   - State field auto-fills with "Kaduna"
   - Strategic/micro catchment fields populate
   - Toast shows: "📍 Location Detected: State: Kaduna..."

#### Scenario 2: State User Validation
1. **Setup:** Login as `state_user` assigned to Kaduna
2. **Action:** Open form while in Kaduna
3. **Expected:** Form opens without validation dialog

#### Scenario 3: State User Outside Boundary
1. **Setup:** Login as `state_user` assigned to Kaduna
2. **Action:** Mock location to Kano, open form
3. **Expected:**
   - Dialog appears: "You are in Kano but assigned to Kaduna..."
   - Only "OK" button (no override)
   - Clicking OK exits form

#### Scenario 4: Federal Admin Override
1. **Setup:** Login as `federal_admin`
2. **Action:** Open form with mismatched location
3. **Expected:**
   - Dialog shows with "Override & Continue" button
   - Override logs event and allows form to continue

---

## Manual Testing with UI

### Accessing the Test UI

**Option 1: Add to AndroidManifest.xml**

Add this activity declaration to `AndroidManifest.xml`:

```xml
<activity
    android:name=".activities.GeofenceTestActivity"
    android:label="Geofence Test"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

**Option 2: Launch via ADB**

```bash
adb shell am start -n org.odk.collect.android/.activities.GeofenceTestActivity
```

### Using the Test UI

1. **Enter State:** Type state name (e.g., "Kaduna")
2. **Enter Coordinates:**
   - Latitude: e.g., `10.5105`
   - Longitude: e.g., `7.4165`
3. **Tap "Test Coordinates"**
4. **View Results:** See detected boundaries in scrollable results area

### Load All States Feature

1. Tap **"Load All States"** button
2. Watch progress as all 20 states load
3. See summary: "✅ Loaded X/20 states successfully"

---

## Test Coordinates

### Kaduna State

| Location | Latitude | Longitude | Expected Results |
|----------|----------|-----------|------------------|
| **Kaduna City Center** | 10.5105 | 7.4165 | State: Kaduna |
| **Hadejia Strategic Catchment** | 10.519886 | 8.775070 | State: Kaduna<br>Strategic: Hadejia |
| **Kaduna North LGA** | 10.5260 | 7.4398 | State: Kaduna<br>LGA: Kaduna North |

### Kano State

| Location | Latitude | Longitude | Expected Results |
|----------|----------|-----------|------------------|
| **Kano City Center** | 12.0022 | 8.5919 | State: Kano |
| **Kano Municipal LGA** | 12.0000 | 8.5167 | State: Kano<br>LGA: Kano Municipal |

### Other States

| Location | Latitude | Longitude | Expected Results |
|----------|----------|-----------|------------------|
| **Abuja (FCT)** | 9.0765 | 7.3986 | State: FCT |
| **Bauchi City** | 10.3158 | 9.8442 | State: Bauchi |
| **Maiduguri (Borno)** | 11.8333 | 13.1500 | State: Borno |
| **Sokoto City** | 13.0622 | 5.2339 | State: Sokoto |

### Edge Cases

| Location | Latitude | Longitude | Expected Results |
|----------|----------|-----------|------------------|
| **Outside Nigeria** | 0.0 | 0.0 | ❌ No boundaries found |
| **Atlantic Ocean** | 4.0 | 3.0 | ❌ Outside all geofences |

---

## Expected Results

### Success Indicators

✅ **State Detection:**
```
🏛️ STATE:
  • Kaduna
    ID: Kaduna-STATE-001
```

✅ **Strategic Catchment:**
```
🌊 STRATEGIC CATCHMENT:
  • Hadejia
    ID: Kaduna-SC-6
    State: Kaduna
```

✅ **Micro Catchment:**
```
💧 MICRO CATCHMENT:
  • MC-Hadejia-001
    ID: Kaduna-MC-45
    State: Kaduna
```

✅ **Form Auto-Populate:**
```
FORM AUTO-POPULATE TEST:
✅ Within Boundaries

Form Fields:
  State: Kaduna
  LGA: Kaduna North
  Strategic Catchment: Hadejia
  Micro Catchment: MC-Hadejia-001
```

### Performance Benchmarks

| Metric | Target | Acceptable |
|--------|--------|------------|
| **Query Time** | < 50ms | < 100ms |
| **State Load Time** | < 2s | < 5s |
| **All States Load** | < 30s | < 60s |
| **Memory Usage** | < 50MB | < 100MB |

---

## Troubleshooting

### Issue 1: No Polygons Found

**Symptoms:**
```
❌ No boundaries found at this location
Point is outside all loaded geofences
```

**Possible Causes:**
1. State data not loaded
2. Coordinates are actually outside boundaries
3. GeoJSON files missing from assets

**Solutions:**
- Verify state is loaded: Check "Load All States" results
- Verify GeoJSON files exist: `collect_app/src/main/assets/geofencing/{State}/`
- Try known coordinates: Use Kaduna City (10.5105, 7.4165)

### Issue 2: Slow Performance

**Symptoms:**
- Query time > 100ms
- App freezes during detection

**Possible Causes:**
1. Too many states loaded simultaneously
2. Bounding box optimization not working
3. Large polygon complexity

**Solutions:**
- Load only needed states
- Check bounding box pre-filtering is active
- Monitor Logcat for performance warnings

### Issue 3: Wrong Location Detected

**Symptoms:**
```
✅ Found polygon but wrong state
Expected: Kaduna
Got: Kano
```

**Possible Causes:**
1. Coordinate order swapped (lat/lon vs lon/lat)
2. Multiple overlapping polygons
3. GeoJSON data error

**Solutions:**
- Verify coordinate order: MapPoint(latitude, longitude)
- Check raw GeoJSON: Coordinates should be [lon, lat]
- Test with multiple known points

### Issue 4: Field Not Auto-Populating

**Symptoms:**
- Form field remains empty
- No toast notification appears

**Possible Causes:**
1. Field name doesn't match known patterns
2. GPS location not available
3. Location permissions denied

**Solutions:**
- Check field name variations: "State", "state", "state_name" all work
- Verify GPS is enabled
- Check location permissions in app settings
- Review Logcat for auto-populate logs

### Issue 5: Build Errors

**Common Errors:**

#### Error: Cannot find symbol
```
error: cannot find symbol GeofenceTestActivity
```
**Solution:** Sync Gradle files (File → Sync Project with Gradle Files)

#### Error: Layout not found
```
error: cannot find layout activity_geofence_test
```
**Solution:** Verify `activity_geofence_test.xml` is in `res/layout/`

#### Error: Missing dependencies
```
error: unresolved reference: kotlinx
```
**Solution:** Add Kotlin coroutines dependency to `build.gradle`

---

## Verification Checklist

Use this checklist to verify all geofencing features:

### Core Functionality
- [ ] State detection works for at least 3 states
- [ ] Strategic catchment detection works
- [ ] Micro catchment detection works
- [ ] LGA detection works (if data available)
- [ ] Points outside boundaries return empty results

### Form Integration
- [ ] Location fields auto-populate
- [ ] Toast notifications appear
- [ ] Select lists populate correctly
- [ ] Text fields populate correctly
- [ ] Fields skip if already answered

### Validation
- [ ] State users blocked if outside boundary
- [ ] Federal users/admins can work anywhere
- [ ] Validation dialog shows for state users
- [ ] Override button appears for admins only
- [ ] Cancel button exits form

### Performance
- [ ] Query time < 100ms
- [ ] State loads in < 5 seconds
- [ ] No UI freezing during detection
- [ ] Memory usage reasonable (< 100MB)

### Edge Cases
- [ ] No GPS: Form continues without validation
- [ ] Outside boundaries: Returns empty results
- [ ] Multiple overlapping polygons: Returns all
- [ ] Invalid coordinates: Handles gracefully

---

## Test Data Summary

### Available GeoJSON Files

Total files: **60 GeoJSON files** across **20 states**

Each state has 3 files:
1. `{State}_strategic_catchments.geojson`
2. `{State}_micro_catchments.geojson`
3. `{State}_interventions.geojson`

### States with Data

Adamawa, Bauchi, Benue, Borno, FCT, Gombe, Jigawa, Kaduna, Kano, Katsina, Kebbi, Kogi, Kwara, Nasarawa, Niger, Plateau, Sokoto, Taraba, Yobe, Zamfara

---

## Reporting Issues

When reporting geofencing issues, please include:

1. **Device Info:** Model, Android version
2. **Coordinates Tested:** Latitude, longitude
3. **State Loaded:** Which state data was loaded
4. **Expected Result:** What should have been detected
5. **Actual Result:** What was actually detected
6. **Logcat Output:** Relevant logs from Timber
7. **Screenshot:** If using test UI

### Logcat Filters

```bash
# Geofencing logs
adb logcat -s "GeofenceManager" "GeofenceFormHelper" "FormFillingActivity"

# Performance logs
adb logcat | grep "query time\|validation\|auto-populate"
```

---

## Next Steps

After completing testing:

1. **Document Results:** Record which states work correctly
2. **Report Issues:** File bugs for any failing tests
3. **Performance Tuning:** Optimize slow queries if needed
4. **User Acceptance Testing:** Test with real field users
5. **Production Deployment:** Deploy to production devices

---

## Summary

This testing framework provides:
- ✅ **11 unit tests** for automated validation
- ✅ **Manual test UI** for interactive testing
- ✅ **30+ test coordinates** across Nigeria
- ✅ **Performance benchmarks** for optimization
- ✅ **Troubleshooting guide** for common issues

All tests validate that the geofencing system correctly identifies states, LGAs, strategic catchments, and micro catchments using real GeoJSON data from the ACReSAL project.

---

**Last Updated:** October 2025
**Version:** 1.0
**Status:** Ready for Testing ✅
