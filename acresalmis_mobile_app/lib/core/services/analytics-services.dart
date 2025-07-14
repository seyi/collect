import 'package:firebase_analytics/firebase_analytics.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter_template/locator.dart';
import 'package:package_info_plus/package_info_plus.dart';

final analytics = getIt<AnalyticsService>();

class AnalyticsService {
  final FirebaseAnalytics _analytics = FirebaseAnalytics.instance;
  late PackageInfo packageInfo;

  initPackageInfo() async {
    try {
      packageInfo = await PackageInfo.fromPlatform();
    } catch (e) {
      debugPrint(e.toString());
    }
  }

  FirebaseAnalyticsObserver getAnalyticsObserver() => FirebaseAnalyticsObserver(analytics: _analytics);
}
