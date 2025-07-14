import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_template/constant/api-routes.dart';
import 'package:flutter_template/constant/constants.dart';
import 'package:flutter_template/core/services/app-cache.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/src/model/request/user.dart';
import 'package:flutter_template/src/model/res_model.dart';
import 'package:flutter_template/src/model/user-response.dart';
import 'package:flutter_template/src/utils/device-id-utils.dart';
import 'package:flutter_template/src/utils/network_exception.dart';

import '../storage-service.dart';
import '../user.service.dart';
import 'base-api.dart';

class UserApiService {
  // final FirebaseMessaging _fcm = FirebaseMessaging.instance;
  StorageService storageService = getIt<StorageService>();
  UserService userService = getIt<UserService>();
  AppCache cache = getIt<AppCache>();

  Future updateUser(UpdateUserRequest request) async {
    try {
      Response response = await connect().put(ApiRoutes.updateUser, data: request.toMap());
      await storeUser(response);
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  storeUser(response) {
    debugPrint("store user");
    GetUserResponse res = userModelFromJson(response.data);
    if (!res.success) return;
    userService.getLocalUser(user: res.data);
    storageService.storeItem(key: currentUser, value: jsonEncode(res.data?.toJson()));
  }

  Future resetPassword({
    required String email,
    required String password,
    required String newPassword,
  }) async {
    Map data = {"email": email, "password": password, "newPassword": newPassword};
    try {
      Response response = await connect().post(ApiRoutes.resetPassword, data: data);

      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future updateTransactionPin({required String pin}) async {
    Map data = {
      "pin": pin,
    };
    try {
      Response response = await connect().patch(ApiRoutes.updatePin, data: data);
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future deleteAccountRequest({required String reason}) async {
    Map data = {
      "reason": reason,
    };
    try {
      Response response = await connect().delete(ApiRoutes.deleteAccount, data: data);
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future sendReview({
    required String review,
    required num rating,
  }) async {
    Map data = {"rating": rating, 'content': review};
    try {
      Response response = await connect().post("reviews", data: data);
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future getUser() async {
    debugPrint("getUser");
    try {
      Response response = await connect().get(ApiRoutes.getUser);
      print(response.data);
      if (response.statusCode == 200 || response.statusCode == 201) {
        await storeUser(response);
      }

      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      print('getUser Error:${e.toString()}');
      return ResModel(message: e.toString());
    }
  }

  Future getNotifications() async {
    print("getNotifications");
    try {
      Response response = await connect().get(ApiRoutes.getNotifications);
      if (response.statusCode == 200 || response.statusCode == 201) {
        print(response.data);
      }
      return resModelFromJson(response.data);
    } on DioError catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future addFCMToken() async {
    print("addFcmToken");

    final token = await getFCMToken();
    var deviceID = await getUniqueDeviceId();
    try {
      Response response = await connect().put("", data: {'token': token, 'deviceId': deviceID});

      debugPrint("addToken Response:::${response.statusCode}");
      return resModelFromJson(response.data);
    } on DioError catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future<String?> getFCMToken() async {
    return await ""; //_fcm.getToken();
  }

//
}
