import 'package:flutter/services.dart';

class OdkCollectIntegration {
  static const platform = MethodChannel('com.acresal.odk_collect');

  Future<void> launchOdkCollect() async {
    try {
      await platform.invokeMethod('launchOdkCollect');
    } on PlatformException catch (e) {
      print("Failed to invoke ODK Collect: '${e.message}'.");
    }
  }
}