// // import 'package:facebook_app_events/facebook_app_events.dart';
// import 'package:firebase_messaging/firebase_messaging.dart';
// import 'package:flutter_local_notifications/flutter_local_notifications.dart';
 import 'package:flutter_template/core/enum/flavors.dart';
//
// // final facebookAppEvents = FacebookAppEvents();
String isFirstLaunch = '';
 String hasToken = '';
// final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin = FlutterLocalNotificationsPlugin();
//
// AndroidNotificationChannel channel = const AndroidNotificationChannel(
//     'high_importance_channel', // id
//     'High Importance Notifications', // title/ description
//     importance: Importance.max,
//     playSound: true,
//     showBadge: true);
//
// Future<void> firebaseMessagingBackgroundHandler(RemoteMessage message) async {
//   print("....");
//   // If you're going to use other Firebase services in the background, such as Firestore,
//   // make sure you call `initializeApp before using other Firebase services.
//
//   const AndroidInitializationSettings initializationSettingsAndroid = AndroidInitializationSettings(
//     '@mipmap/ic_launcher',
//   );
//
//   /// Note: permissions aren't requested here just to demonstrate that can be
//   /// done later
//   ///
//   ///
//
//   const DarwinInitializationSettings initializationSettingsIOS = DarwinInitializationSettings(
//     requestAlertPermission: false,
//     requestBadgePermission: false,
//     requestSoundPermission: false,
//   );
//
//   const InitializationSettings initializationSettings = InitializationSettings(android: initializationSettingsAndroid, iOS: initializationSettingsIOS);
//   // await Firebase.initializeApp();
//   await flutterLocalNotificationsPlugin.initialize(
//     initializationSettings,
//     // onSelectNotification: (String? payload) async {}
//   );
//
//   RemoteNotification? notification = message.notification;
//   AndroidNotification? android = message.notification?.android;
//   if (notification != null && android != null) {
//     flutterLocalNotificationsPlugin.show(
//         notification.hashCode,
//         notification.title,
//         notification.body,
//         NotificationDetails(
//           android: AndroidNotificationDetails(channel.id, channel.name,
//               icon: '@mipmap/ic_launcher',
//               importance: Importance.max,
//               priority: Priority.max,
//               playSound: true,
//               largeIcon: const FilePathAndroidBitmap('@mipmap/ic_launcher'),
//               styleInformation: const BigPictureStyleInformation(FilePathAndroidBitmap('@mipmap/ic_launcher'))),
//         ));
//     print('Handling a background message ${message.messageId} message handler 1');
//     print("Message Payload is ${message.data.toString()}");
//   }
// }
//
// Flavor? flavor;
