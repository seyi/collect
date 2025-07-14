import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_template/constant/api-routes.dart';
import 'package:flutter_template/constant/constants.dart';
import 'package:flutter_template/core/services/app-cache.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/src/model/login-response.dart';
import 'package:flutter_template/src/model/res_model.dart';
import 'package:flutter_template/src/utils/network_exception.dart';

import '../storage-service.dart';
import '../user.service.dart';
import 'base-api.dart';

class TransactionsApiService {
  StorageService storageService = getIt<StorageService>();
  UserService userService = getIt<UserService>();
  AppCache cache = getIt<AppCache>();

  storeUser(response) {
    debugPrint("store user");
    ResModel res = resModelFromJson(response.data);
    if (!res.success!) return;
    userService.getLocalUser(user: ACUser.fromJson(res.data));
    storageService.storeItem(key: currentUser, value: jsonEncode(res.data));
  }

  Future template4() async {
    try {
      Response response = await connect().get("");
      await storeUser(response);
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return e.toString();
    }
  }

  Future template() async {
    debugPrint("getAirtime");
    try {
      Response response = await connect().get(ApiRoutes.getAirtime);
      print(response.data);
      if (response.statusCode == 200 || response.statusCode == 201) {}

      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      print('getUser Error:${e.toString()}');
      return ResModel(message: e.toString());
    }
  }

  Future purchaseAirtime({dynamic data}) async {
    debugPrint("purchaseAirtime");
    try {
      Response response = await connect().post(ApiRoutes.buyAirtime, data: data?.toJson());
      print(response.data);
      if (response.statusCode == 200 || response.statusCode == 201) {}
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      print('getUser Error:${e.toString()}');
      return ResModel(message: e.toString());
    }
  }

  Future template1() async {
    debugPrint("getAirtime");
    try {
      Response response = await connect().get(
        "",
      );
      print(response.data);
      if (response.statusCode == 200 || response.statusCode == 201) {}

      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      print(' Error:${e.toString()}');
      return ResModel(message: e.toString());
    }
  }

  Future template2({String? id}) async {
    debugPrint("getAirtime");
    try {
      Response response = await connect().get("");
      print(response.data);
      if (response.statusCode == 200 || response.statusCode == 201) {}

      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      print(' Error:${e.toString()}');
      return ResModel(message: e.toString());
    }
  }

  Future template3({dynamic data}) async {
    debugPrint("template3");
    try {
      Response response = await connect().post(ApiRoutes.buyData, data: data?.toJson());
      print(response.data);
      if (response.statusCode == 200 || response.statusCode == 201) {}
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      print('template3 Error:${e.toString()}');
      return ResModel(message: e.toString());
    }
  }

  storeNewAccessToken(Response response) async {
    debugPrint("storeNewAccessToken");
    ResModel res = resModelFromJson(response.data);
    if (!res.success!) return;
    String? newToken = res.data['newAccessToken'] ?? "";
    if (newToken != null && newToken.isNotEmpty) {
      storageService.deleteItem(key: bearerToken);
      storageService.storeItem(key: bearerToken, value: newToken);
    }
  }
}
