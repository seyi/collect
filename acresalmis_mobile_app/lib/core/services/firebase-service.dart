// import 'dart:io';
//
// import 'package:dio/dio.dart';
// import 'package:firebase_messaging/firebase_messaging.dart';
// import 'package:flutter/foundation.dart';
// import 'package:flutter_local_notifications/flutter_local_notifications.dart';
// import 'package:app/core/services/notification-handler.dart';
// import 'package:app/core/services/user.service.dart';
// import 'package:app/core/services/web-services/base-api.dart';
// import 'package:app/core/services/web-services/user-api-service.dart';
// import 'package:app/locator.dart';
// import 'package:app/src/model/res_model.dart';
// import 'package:app/src/utils/device-id-utils.dart';
//
// class FirebaseService {
//   final FirebaseMessaging _fcm = FirebaseMessaging.instance;
//   final UserService user = getIt<UserService>();
//   final userApi = getIt<UserApiService>();
//
//   Future<String?> getFCMToken() async {
//     return await _fcm.getToken();
//   }
//
//   void actions(Map<String, dynamic> message) async {
//     if (kDebugMode) {
//       print("Notification payload is ${message.toString()}");
//     }
//     final action = message["action"];
//     switch (action) {
//       case "update":
//         // await _checkForUpdates();
//         break;
//       default:
//         if (kDebugMode) {
//           print("default case");
//         }
//     }
//   }
//
//   Future addToken() async {
//     debugPrint("add token");
//     var token = await getFCMToken();
//
//     try {
//       Response response = await connect().patch("/user/token/add", data: {"token": token});
//       print("addToken Response:::${response.statusCode}");
//       return resModelFromJson(response.data);
//     } on DioError catch (e) {
//       debugPrint("Add Token Error: ${e.response}");
//       return resModelFromJson(e.response?.data);
//     } catch (e) {
//       debugPrint("not:::${e.toString()}");
//     }
//   }
//
//   Future test() async {
//     var deviceID = await getUniqueDeviceId();
//     var token = await getFCMToken();
//
//     try {
//       Response response = await connect(useCustomUrl: "https://e9ea-102-89-44-46.ngrok-free.app/v1/customers/fcm")
//           // useCustomUrl:
//           //     "https://8a20-102-89-42-232.ngrok-free.app/v1/customers/fcm/")
//           .put("", data: {"token": token, "deviceId": deviceID});
//       print(response.data);
//       return resModelFromJson(response.data);
//     } on DioError catch (e) {
//       print(e.message.toString());
//       debugPrint("Add Token Error: ${e.response}");
//       return resModelFromJson(e.response?.data);
//     } catch (e) {
//       print(e.toString());
//       debugPrint(e.toString());
//     }
//   }
//
//   void setupNotificationListeners() async {
//     try {
//       if (Platform.isIOS) {
//         NotificationSettings settings = await FirebaseMessaging.instance.requestPermission(
//           alert: true,
//           announcement: false,
//           badge: true,
//           carPlay: false,
//           criticalAlert: false,
//           provisional: false,
//           sound: true,
//         );
//       }
//
//       FirebaseMessaging.instance.getInitialMessage().then((RemoteMessage? message) async {
//         debugPrint("get initial message");
//
//         if (message != null) {
//           actions(message.data);
//         }
//       });
//
//       // FirebaseMessaging.
//       FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) async {
//         debugPrint('A new onMessageOpenedApp event was published!');
//         actions(message.data);
//       });
//
//       FirebaseMessaging.onMessage.listen((RemoteMessage message) async {
//         debugPrint(message.data.toString());
//         actions(message.data);
//         if (message.data['action'].toString().toLowerCase() == "logout") {
//           debugPrint("yes");
//           //TODO: auto log user out at this point
//           // autoLogUserOut();
//         }
//
//         await Future.delayed(const Duration(milliseconds: 500), () async {
//           await userApi.getUser();
//         });
//
//         RemoteNotification? notification = message.notification;
//         AndroidNotification? android = message.notification?.android;
//         if (notification != null && android != null) {
//           flutterLocalNotificationsPlugin.show(
//               notification.hashCode,
//               notification.title,
//               notification.body,
//               NotificationDetails(
//                 android: AndroidNotificationDetails(channel.id, channel.name,
//                     icon: '@mipmap/ic_launcher',
//                     largeIcon: const FilePathAndroidBitmap('@mipmap/ic_launcher'),
//                     styleInformation: const BigPictureStyleInformation(FilePathAndroidBitmap('@mipmap/ic_launcher'))),
//               ));
//         }
//       });
//     } catch (e) {
//       print(e.toString());
//     }
//   }
// }
