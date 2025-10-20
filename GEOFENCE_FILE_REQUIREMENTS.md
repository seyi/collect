# Geofence File Requirements

**Last Updated:** October 2025
**Status:** ✅ CURRENT

---

## File Priority Levels

### ✅ REQUIRED (Critical)

**STATE Boundary Files**
- **File Pattern:** `{State}_state_boundary.geojson`
- **Example:** `Kaduna_state_boundary.geojson`
- **Purpose:** Define the outer boundary of the state
- **Impact if Missing:** Load will FAIL, user will see error toast

**Strategic Catchment Files**
- **File Pattern:** `{State}_strategic_catchments.geojson`
- **Example:** `Kaduna_strategic_catchments.geojson`
- **Purpose:** Define strategic catchment areas (large watersheds)
- **Impact if Missing:** Load will FAIL, user will see error toast

**Micro Catchment Files**
- **File Pattern:** `{State}_micro_catchments.geojson`
- **Example:** `Kaduna_micro_catchments.geojson`
- **Purpose:** Define micro catchment subdivisions (smaller watersheds)
- **Impact if Missing:** Load will FAIL, user will see error toast

### ⚠️ OPTIONAL

**LGA Boundary Files**
- **File Pattern:** `{State}_lga_boundaries.geojson`
- **Example:** `Kaduna_lga_boundaries.geojson`
- **Purpose:** Define Local Government Area boundaries within the state
- **Impact if Missing:** Load will SUCCEED, LGA detection will show "Not detected"

---

## Directory Structure

```
assets/
└── geofencing/
    ├── Kaduna/
    │   ├── Kaduna_state_boundary.geojson         ✅ REQUIRED
    │   ├── Kaduna_lga_boundaries.geojson         ⚠️ Optional
    │   ├── Kaduna_strategic_catchments.geojson   ✅ REQUIRED
    │   └── Kaduna_micro_catchments.geojson       ✅ REQUIRED
    ├── Kano/
    │   ├── Kano_state_boundary.geojson           ✅ REQUIRED
    │   ├── Kano_lga_boundaries.geojson           ⚠️ Optional
    │   ├── Kano_strategic_catchments.geojson     ✅ REQUIRED
    │   └── Kano_micro_catchments.geojson         ✅ REQUIRED
    └── [... 18 other states ...]
```

---

## Loading Behavior

### Scenario 1: All Files Present
```
Loading geofences for Kaduna...
✅ Loaded 1 STATE polygons for Kaduna
✅ Loaded 23 LGA polygons for Kaduna
✅ Loaded 5 STRATEGIC_CATCHMENT polygons for Kaduna
✅ Loaded 15 MICRO_CATCHMENT polygons for Kaduna
Geofence loading complete. Loaded 1 states, 44 total polygons

Toast: "Loaded 44 boundaries for Kaduna"
Result: SUCCESS ✅
```

### Scenario 2: STATE + Strategic + Micro (Without LGA)
```
Loading geofences for Kaduna...
✅ Loaded 1 STATE polygons for Kaduna
ℹ️ Optional LGA polygons not found for Kaduna (this is OK)
✅ Loaded 5 STRATEGIC_CATCHMENT polygons for Kaduna
✅ Loaded 15 MICRO_CATCHMENT polygons for Kaduna
Geofence loading complete. Loaded 1 states, 21 total polygons

Toast: "Loaded 21 boundaries for Kaduna"
Result: SUCCESS ✅
```

### Scenario 3: Required File Missing (Error)
```
Loading geofences for Kaduna...
❌ File not found: geofencing/Kaduna/Kaduna_strategic_catchments.geojson
⚠️ Critical: STRATEGIC_CATCHMENT not found for Kaduna
Geofence loading complete. Loaded 0 states, 0 total polygons

Toast: "Warning: Failed to load boundary data"
Result: FAILED ❌
```

**Note:** The same error occurs if STATE or MICRO_CATCHMENT files are missing

---

## Development Dialog Display

### With All Files
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

### Without LGA File (Valid Configuration)
```
📍 GPS Coordinates:
Latitude: 10.510500
Longitude: 7.416500
Accuracy: 15.0 meters

🗺️ Detected Boundaries:

State:
  Kaduna ✅

LGA:
  Not detected

Strategic Catchment:
  Hadejia ✅

Micro Catchment:
  MC-Hadejia-001 ✅

📊 Total polygons found: 3
```

**Note:** This is perfectly valid! LGA is optional.

---

## Location Validation Behavior

### With All Required Files (STATE, Strategic, Micro)

**State User in Kaduna:**
- ✅ Inside Kaduna state boundary → Validation PASSES
- ❌ Outside Kaduna state boundary → Validation FAILS

**Form Auto-Population:**
- State field: Auto-populated (e.g., "Kaduna") ✅
- LGA field: Auto-populated if LGA file present, otherwise manual entry
- Strategic Catchment: Auto-populated (e.g., "Hadejia") ✅
- Micro Catchment: Auto-populated (e.g., "MC-Hadejia-001") ✅

---

## GeoJSON Format

### STATE Boundary File

**Required Properties:**
- `name`: State name (must match folder name)
- `id`: Unique identifier (e.g., "Kaduna-STATE-1")
- `state`: State name

**Example:** `Kaduna_state_boundary.geojson`

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
            [7.100000, 10.100000],
            [7.200000, 10.200000],
            [7.300000, 10.300000],
            [7.100000, 10.100000]
          ]
        ]
      }
    }
  ]
}
```

### LGA Boundaries File (Optional)

**Required Properties:**
- `name`: LGA name
- `id`: Unique identifier (e.g., "Kaduna-LGA-001")
- `state`: State name

**Example:** `Kaduna_lga_boundaries.geojson`

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
        "coordinates": [[[...]]]
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
        "coordinates": [[[...]]]
      }
    }
  ]
}
```

---

## Deployment Strategies

### Strategy 1: Standard Deployment (Without LGA)

**Pros:**
- ✅ All critical features work
- ✅ State validation works
- ✅ Catchment detection works
- ✅ Smaller APK size (no LGA data)

**Cons:**
- ⚠️ Users must enter LGA manually

**Use Cases:**
- Standard production deployment
- When LGA data not available
- When LGA not critical for workflow

**Required Files:** 60 files (3 per state × 20 states)
- STATE boundary
- Strategic catchments
- Micro catchments

---

### Strategy 2: Full Deployment (All Files)

**Pros:**
- ✅ Complete automation
- ✅ All fields auto-populated
- ✅ Full geofencing features
- ✅ Better user experience

**Cons:**
- ⚠️ Larger APK size
- ⚠️ Longer deployment time
- ⚠️ More data preparation required

**Use Cases:**
- Production deployment
- States with complete data
- Maximum automation desired

**Required Files:** 80 files (4 per state × 20 states)

---


---

## File Size Guidelines

### Recommended Maximum Sizes

- **STATE boundary:** < 50 KB (single polygon)
- **LGA boundaries:** < 500 KB (multiple polygons)
- **Strategic catchments:** < 200 KB (5-10 polygons)
- **Micro catchments:** < 300 KB (10-20 polygons)

### Optimization Tips

1. **Reduce coordinate precision** to 6 decimal places (~11cm accuracy)
   ```python
   # Python example
   coordinates = [[round(coord, 6) for coord in point] for point in polygon]
   ```

2. **Simplify polygons** using GIS tools
   - QGIS: Vector → Geometry Tools → Simplify
   - Douglas-Peucker algorithm
   - Target: Reduce vertices by 50-70%

3. **Remove unnecessary properties**
   - Keep only: `name`, `id`, `state`
   - Remove: `area`, `perimeter`, `description`, etc.

---

## Validation Script

Use this to verify your GeoJSON files:

```bash
#!/bin/bash
# validate_geojson.sh

STATES=(Adamawa Bauchi Benue Borno Fct Gombe Jigawa Kaduna Kano Katsina Kebbi Kogi Kwara Nasarawa Niger Plateau Sokoto Taraba Yobe Zamfara)

echo "Checking STATE boundary files (REQUIRED)..."
for state in "${STATES[@]}"; do
    file="assets/geofencing/$state/${state}_state_boundary.geojson"
    if [ -f "$file" ]; then
        echo "✅ $state: STATE boundary found"
    else
        echo "❌ $state: STATE boundary MISSING (CRITICAL)"
    fi
done

echo ""
echo "Checking optional files..."
for state in "${STATES[@]}"; do
    lga="assets/geofencing/$state/${state}_lga_boundaries.geojson"
    strategic="assets/geofencing/$state/${state}_strategic_catchments.geojson"
    micro="assets/geofencing/$state/${state}_micro_catchments.geojson"

    [ -f "$lga" ] && echo "✅ $state: LGA boundaries found" || echo "⚠️  $state: LGA boundaries missing (optional)"
    [ -f "$strategic" ] && echo "✅ $state: Strategic catchments found" || echo "⚠️  $state: Strategic catchments missing (optional)"
    [ -f "$micro" ] && echo "✅ $state: Micro catchments found" || echo "⚠️  $state: Micro catchments missing (optional)"
done
```

---

## Frequently Asked Questions

### Q: What are the minimum required files?
**A:** You need 3 files per state:
- STATE boundary (required)
- Strategic catchments (required)
- Micro catchments (required)

LGA is optional.

### Q: Can I add LGA files later?
**A:** Yes! You can deploy without LGA files, then add them in future app updates.

### Q: Will the app crash if LGA file is missing?
**A:** No. The system logs a debug message saying "Optional LGA polygons not found (this is OK)" and continues normally.

### Q: What if STATE or catchment files are missing?
**A:** The load will FAIL for that state. The user will see "Warning: Failed to load boundary data" toast message.

### Q: Can different states have different file combinations?
**A:** Yes, but all states must have the 3 required files. For example:
- Kaduna: STATE + LGA + Strategic + Micro (all 4 files) ✅
- Kano: STATE + Strategic + Micro (3 required files, no LGA) ✅
- Bauchi: STATE only ❌ WILL FAIL (missing catchments)

Only the first two combinations are valid.

---

## Implementation Checklist

### Standard Deployment (Without LGA)

- [ ] Create STATE boundary GeoJSON for all 20 states
- [ ] Create Strategic catchment GeoJSON for all 20 states
- [ ] Create Micro catchment GeoJSON for all 20 states
- [ ] Validate GeoJSON format with online validator
- [ ] Place files in `assets/geofencing/{STATE}/` directory
- [ ] Verify file naming matches patterns
- [ ] Test with at least 3 state users
- [ ] Verify location validation works
- [ ] Confirm development dialog shows 3 boundary types (no LGA)

### Full Deployment (With LGA)

- [ ] All items from Standard Deployment
- [ ] Create LGA boundaries GeoJSON for all 20 states
- [ ] Optimize file sizes (reduce precision, simplify polygons)
- [ ] Test with complete file set
- [ ] Verify all 4 boundary types detected
- [ ] Confirm form auto-population works for all fields including LGA

---

**Related Documentation:**
- `GEOFENCE_LOADING_PLAN.md` - Implementation plan
- `GEOFENCE_LOADING_IMPLEMENTATION.md` - Implementation summary
- `GEOFENCING_TESTING_GUIDE.md` - Testing procedures

---

**Last Updated:** October 2025
**Priority:** ✅ REQUIRED: STATE + STRATEGIC CATCHMENT + MICRO CATCHMENT | OPTIONAL: LGA
**Status:** 📋 READY FOR DEPLOYMENT
