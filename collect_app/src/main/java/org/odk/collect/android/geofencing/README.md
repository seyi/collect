# Geofencing Module

Location-based geofencing for ACReSAL Collect app. Determines if user's GPS location is within predefined polygons (states, catchments, intervention sites).

## Package Contents

### Data Models
- **`GeofenceType.kt`** - Enum for geofence categories
- **`BoundingBox.kt`** - Fast spatial filtering using rectangular bounds
- **`GeoFencePolygon.kt`** - Main polygon model with metadata
- **`CacheStats.kt`** - Statistics about loaded data

### Core Classes
- **`GeoJsonParser.kt`** - Parse GeoJSON files from assets
- **`GeofencingUtils.kt`** - Point-in-polygon algorithm (Ray Casting)
- **`GeoFenceManager.kt`** - Singleton manager for loading and querying polygons

## Quick Start

### 1. Load Geofences

```kotlin
// In your Activity or Application class
lifecycleScope.launch {
    val manager = GeoFenceManager.getInstance(context)

    // Load for specific state (for state users)
    val success = manager.loadGeofences("Kaduna")

    // Or load all states (for federal users)
    val success = manager.loadGeofences(null)
}
```

### 2. Check if Point is in Geofence

```kotlin
val manager = GeoFenceManager.getInstance(context)
val currentLocation = MapPoint(latitude = 10.52, longitude = 8.77)

// Find all containing polygons
val polygons = manager.getContainingPolygons(currentLocation)

if (polygons.isNotEmpty()) {
    val state = polygons.firstOrNull { it.type == GeofenceType.STATE }
    val catchment = polygons.firstOrNull { it.type == GeofenceType.STRATEGIC_CATCHMENT }

    println("You are in ${state?.name}, ${catchment?.name}")
}
```

### 3. Get Specific Polygon

```kotlin
val polygon = manager.getPolygonById("Kaduna-SC-6")

if (polygon != null) {
    val isInside = polygon.contains(currentLocation)
    val distance = GeofencingUtils.distanceToPolygon(currentLocation, polygon.vertices)
}
```

## Data Structure

GeoJSON files are located in `assets/geofencing/{State}/`:

```
assets/geofencing/
├── Kaduna/
│   ├── Kaduna_strategic_catchments.geojson
│   ├── Kaduna_micro_catchments.geojson
│   └── Kaduna_interventions.geojson
├── Bauchi/
│   ├── Bauchi_strategic_catchments.geojson
│   └── ...
└── ... (20 states total)
```

## API Reference

### GeoFenceManager

```kotlin
// Singleton instance
val manager = GeoFenceManager.getInstance(context)

// Load geofences
suspend fun loadGeofences(state: String? = null): Boolean

// Query methods
fun getContainingPolygons(point: MapPoint, types: List<GeofenceType>? = null): List<GeoFencePolygon>
fun getPolygonById(id: String): GeoFencePolygon?
fun getPolygonsForState(state: String, type: GeofenceType): List<GeoFencePolygon>
fun isPointInPolygon(point: MapPoint, polygonId: String): Boolean

// Utility methods
fun getCacheStats(): CacheStats
fun isLoaded(): Boolean
fun isStateLoaded(state: String): Boolean
fun clearCache()
```

### GeoFencePolygon

```kotlin
data class GeoFencePolygon(
    val id: String,                    // "Kaduna-SC-6"
    val name: String,                  // "Hadejia"
    val type: GeofenceType,            // STRATEGIC_CATCHMENT
    val state: String,                 // "Kaduna"
    val vertices: List<MapPoint>,      // Boundary coordinates
    val properties: Map<String, Any>,  // Additional GeoJSON properties
    val boundingBox: BoundingBox       // For optimization
)

// Methods
fun contains(point: MapPoint): Boolean
fun getPropertyAsString(key: String, default: String = ""): String
fun getPropertyAsDouble(key: String, default: Double = 0.0): Double
```

### GeofencingUtils

```kotlin
object GeofencingUtils {
    // Point-in-polygon test (Ray Casting algorithm)
    fun isPointInPolygon(point: MapPoint, polygon: List<MapPoint>): Boolean

    // Distance calculations
    fun distanceToPolygon(point: MapPoint, polygon: List<MapPoint>): Double
    fun haversineDistance(point1: MapPoint, point2: MapPoint): Double

    // Utility functions
    fun calculatePolygonArea(polygon: List<MapPoint>): Double
    fun calculateCentroid(polygon: List<MapPoint>): MapPoint
}
```

## Performance

- **Load Time:** ~2-5 seconds for all 20 states
- **Query Time:** <50ms per point-in-polygon check
- **Memory Usage:** ~20-30 MB for all polygons
- **Optimization:** Bounding box pre-check eliminates 90%+ of polygons

## Example Usage

### Check User Location Before Form Submission

```kotlin
suspend fun validateUserLocation(userState: String): Boolean {
    val manager = GeoFenceManager.getInstance(this)

    // Ensure geofences are loaded
    if (!manager.isStateLoaded(userState)) {
        manager.loadGeofences(userState)
    }

    val currentLocation = locationTracker.getCurrentLocation()
    val point = MapPoint(currentLocation.latitude, currentLocation.longitude)

    val containingStates = manager.getContainingPolygons(
        point,
        listOf(GeofenceType.STATE)
    )

    val inCorrectState = containingStates.any { it.state == userState }

    if (!inCorrectState) {
        showError("You are not in your assigned state: $userState")
        return false
    }

    return true
}
```

### Auto-populate Form Fields

```kotlin
fun autoPopulateLocationFields(currentLocation: MapPoint): Map<String, String> {
    val manager = GeoFenceManager.getInstance(this)
    val polygons = manager.getContainingPolygons(currentLocation)

    return mapOf(
        "State" to (polygons.firstOrNull { it.type == GeofenceType.STATE }?.name ?: ""),
        "stra_catchment" to (polygons.firstOrNull { it.type == GeofenceType.STRATEGIC_CATCHMENT }?.name ?: ""),
        "micro_catchment" to (polygons.firstOrNull { it.type == GeofenceType.MICRO_CATCHMENT }?.name ?: "")
    )
}
```

## Testing

```kotlin
@Test
fun `point inside Kaduna state`() = runTest {
    val manager = GeoFenceManager.getInstance(context)
    manager.loadGeofences("Kaduna")

    val pointInKaduna = MapPoint(10.52, 8.77)
    val polygons = manager.getContainingPolygons(pointInKaduna)

    assertTrue(polygons.any { it.state == "Kaduna" })
}
```

## Troubleshooting

**Q: No polygons found for my location**
- Check GPS accuracy (must be < 100m)
- Ensure correct state is loaded
- Verify location is actually within project area

**Q: Loading takes too long**
- Only load states for the current user (not all 20)
- Use background thread (coroutines)
- Consider caching in SharedPreferences

**Q: Memory usage too high**
- Clear unused states: `manager.clearStateCache("StateName")`
- Only load needed geofence types

## See Also

- [GEOFENCING_IMPLEMENTATION_PLAN.md](../../../../GEOFENCING_IMPLEMENTATION_PLAN.md) - Full implementation plan
- [Point-in-Polygon Algorithm](https://en.wikipedia.org/wiki/Point_in_polygon)
- [GeoJSON Specification](https://tools.ietf.org/html/rfc7946)
