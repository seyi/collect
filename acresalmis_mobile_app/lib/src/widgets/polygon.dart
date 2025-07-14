import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:geolocator/geolocator.dart';
import 'package:http/http.dart' as http;
import 'package:latlong2/latlong.dart';

class PolygonDrawingMap extends StatefulWidget {
  @override
  _PolygonDrawingMapState createState() => _PolygonDrawingMapState();
}

class _PolygonDrawingMapState extends State<PolygonDrawingMap> {
  final List<LatLng> _polygonPoints = [];
  final List<Map<String, dynamic>> _savedLocations = [];
  final MapController _mapController = MapController();
  bool _isDrawing = false;
  LatLng? _currentLocation;
  String _currentLocationName = "Unknown Location";
  String _searchQuery = '';
  List<Map<String, dynamic>> _searchResults = [];

  @override
  void initState() {
    super.initState();
    _setCurrentLocation();
  }

  Future<void> _setCurrentLocation() async {
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      await Geolocator.requestPermission();
    }
    LocationPermission permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied || permission == LocationPermission.deniedForever) {
      permission = await Geolocator.requestPermission();
    }
    if (permission == LocationPermission.whileInUse || permission == LocationPermission.always) {
      Position position = await Geolocator.getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
      setState(() {
        _currentLocation = LatLng(position.latitude, position.longitude);
        _getLocationName(position.latitude, position.longitude);
      });
    }
  }

  Future<void> _getLocationName(double lat, double lon) async {
    final url = 'https://nominatim.openstreetmap.org/reverse?lat=$lat&lon=$lon&format=json';
    final response = await http.get(Uri.parse(url));
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      setState(() {
        _currentLocationName = data['display_name'] ?? "Unknown Location";
      });
    }
  }

  Future<void> _searchLocation(String query) async {
    if (query.isEmpty) {
      setState(() {
        _searchResults = [];
      });
      return;
    }

    final url = 'https://nominatim.openstreetmap.org/search?q=$query&format=json';
    final response = await http.get(Uri.parse(url));

    if (response.statusCode == 200) {
      final List data = json.decode(response.body);
      setState(() {
        _searchResults = data
            .map<Map<String, dynamic>>((result) => {
                  "display_name": result["display_name"],
                  "lat": double.parse(result["lat"]),
                  "lon": double.parse(result["lon"]),
                })
            .toList();
      });
    }
  }

  void _saveLocation() {
    if (_polygonPoints.isNotEmpty) {
      // Save polygon data
      _savedLocations.add({
        "name": "Polygon at $_currentLocationName",
        "type": "polygon",
        "points": _polygonPoints
            .map((point) => {
                  "latitude": point.latitude,
                  "longitude": point.longitude,
                  "altitude": 0.0, // Mocked altitude as it's not retrieved
                })
            .toList(),
      });
    } else if (_currentLocation != null) {
      // Save current location
      _savedLocations.add({
        "name": _currentLocationName,
        "type": "location",
        "latitude": _currentLocation!.latitude,
        "longitude": _currentLocation!.longitude,
        "altitude": 0.0, // Mocked altitude as it's not retrieved
      });
    }

    setState(() {
      _polygonPoints.clear();
    });

    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Location saved successfully!')),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Draw Polygon on Map'),
        actions: [
          IconButton(
            icon: Icon(
              _isDrawing ? Icons.stop : Icons.edit,
              color: _isDrawing ? Colors.red : null,
            ),
            onPressed: () {
              setState(() {
                _isDrawing = !_isDrawing; // Toggle drawing mode
              });
            },
          ),
          IconButton(
            icon: const Icon(Icons.clear),
            onPressed: () {
              setState(() {
                _polygonPoints.clear(); // Clear all points
              });
            },
          ),
        ],
      ),
      body: Stack(
        children: [
          FlutterMap(
            mapController: _mapController,
            options: MapOptions(
              initialCenter: _currentLocation ?? const LatLng(51.505, -0.09),
              initialZoom: 13.0,
              onTap: (_, point) {
                if (_isDrawing) {
                  setState(() {
                    _polygonPoints.add(point);
                  });
                }
              },
              interactionOptions: const InteractionOptions(
                flags: InteractiveFlag.all,
              ),
            ),
            children: [
              TileLayer(
                urlTemplate: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                subdomains: ['a', 'b', 'c'],
              ),
              if (_polygonPoints.isNotEmpty)
                PolygonLayer(
                  polygons: [
                    Polygon(
                      points: _polygonPoints,
                      borderColor: Colors.blue,
                      borderStrokeWidth: 3.0,
                      color: Colors.blue.withOpacity(0.3),
                    ),
                  ],
                ),
              MarkerLayer(
                markers: [
                  if (_currentLocation != null)
                    Marker(
                      point: _currentLocation!,
                      child: Container(
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          color: Colors.blue.withOpacity(0.4),
                        ),
                        child: const SizedBox(
                          width: 50,
                          height: 50,
                        ),
                      ),
                    ),
                  ..._polygonPoints.map(
                    (point) => Marker(
                      point: point,
                      child: const Icon(
                        Icons.location_on,
                        color: Colors.red,
                        size: 30,
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
          Positioned(
            top: 20,
            left: 20,
            right: 20,
            child: Column(
              children: [
                TextField(
                  onChanged: (value) {
                    setState(() {
                      _searchQuery = value;
                    });
                    _searchLocation(value);
                  },
                  decoration: InputDecoration(
                    hintText: 'Search location',
                    filled: true,
                    fillColor: Colors.white,
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(8),
                      borderSide: BorderSide.none,
                    ),
                    contentPadding: const EdgeInsets.all(10),
                    suffixIcon: IconButton(
                      icon: const Icon(Icons.search),
                      onPressed: () {
                        _searchLocation(_searchQuery);
                      },
                    ),
                  ),
                ),
                if (_searchResults.isNotEmpty)
                  Container(
                    margin: const EdgeInsets.only(top: 8),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(8),
                      boxShadow: [
                        const BoxShadow(
                          color: Colors.black26,
                          blurRadius: 4,
                          offset: Offset(0, 2),
                        ),
                      ],
                    ),
                    child: ListView.builder(
                      shrinkWrap: true,
                      itemCount: _searchResults.length,
                      itemBuilder: (context, index) {
                        final result = _searchResults[index];
                        return ListTile(
                          title: Text(result["display_name"]),
                          onTap: () {
                            final LatLng location = LatLng(
                              result["lat"],
                              result["lon"],
                            );
                            _mapController.move(location, 16);
                            setState(() {
                              _searchResults.clear();
                            });
                          },
                        );
                      },
                    ),
                  ),
              ],
            ),
          ),
          Positioned(
            bottom: 80,
            right: 20,
            child: Column(
              children: [
                FloatingActionButton(
                  mini: true,
                  onPressed: () {
                    _mapController.move(
                      _mapController.camera.center,
                      _mapController.camera.zoom + 1,
                    );
                  },
                  child: const Icon(Icons.zoom_in),
                ),
                const SizedBox(height: 10),
                FloatingActionButton(
                  mini: true,
                  onPressed: () {
                    _mapController.move(
                      _mapController.camera.center,
                      _mapController.camera.zoom - 1,
                    );
                  },
                  child: const Icon(Icons.zoom_out),
                ),
                const SizedBox(height: 10),
                FloatingActionButton(
                  mini: true,
                  onPressed: _saveLocation,
                  child: const Icon(Icons.save),
                ),
              ],
            ),
          ),
          Positioned(
            bottom: 20,
            right: 20,
            child: FloatingActionButton(
              onPressed: () {
                if (_currentLocation != null) {
                  _mapController.move(_currentLocation!, 16);
                }
              },
              child: const Icon(Icons.my_location),
            ),
          ),
        ],
      ),
    );
  }
}
