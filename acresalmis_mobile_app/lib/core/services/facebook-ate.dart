import 'package:app_tracking_transparency/app_tracking_transparency.dart';

Future<void> initializeAppTrackingAndFacebook() async {
  // Request tracking authorization
  final status = await AppTrackingTransparency.requestTrackingAuthorization();

  if (status == TrackingStatus.authorized) {
    // User allowed tracking
    // await facebookAppEvents.setAdvertiserTracking(enabled: true);
  } else {
    // User denied tracking
    // await facebookAppEvents.setAdvertiserTracking(enabled: false);
  }
}
