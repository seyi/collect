import 'package:flutter/material.dart';

class NavObserver extends NavigatorObserver {
  @override
  void didPush(Route<dynamic> route, Route<dynamic>? previousRoute) {
    super.didPush(route, previousRoute);
    if (route is MaterialPageRoute) {
      final routeName = route.settings.name;
      print("Navigated to: $routeName");
      print("Action: Push");
    }
  }

  @override
  void didPop(Route<dynamic> route, Route<dynamic>? previousRoute) {
    super.didPop(route, previousRoute);
    if (route is MaterialPageRoute) {
      final routeName = route.settings.name;
      print("Navigated from: $routeName");
      print("Action: Pop");
    }
  }
}
