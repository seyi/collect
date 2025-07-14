import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_template/constant/api-routes.dart';
import 'package:flutter_template/constant/constants.dart';
import 'package:flutter_template/core/services/storage-service.dart';
import 'package:flutter_template/core/services/user.service.dart';
import 'package:flutter_template/core/services/web-services/user-api-service.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/src/model/login-response.dart';
import 'package:flutter_template/src/model/request/auth.dart';
import 'package:flutter_template/src/model/res_model.dart';
import 'package:flutter_template/src/utils/network_exception.dart';
import 'package:flutter_template/src/utils/string%20utils.dart';

import 'base-api.dart';

class ComponentsApiService {
  StorageService storageService = getIt<StorageService>();
  UserService userService = getIt<UserService>();
  UserApiService userApiService = getIt<UserApiService>();

  Future login(LoginRequest request) async {
    try {
      Response response = await ApiClient.instance.post(ApiRoutes.loginRoute, data: request.toAcreasalLogin());
      print(response.data);
      await storeTokenOnly(response.data.toString().replaceAll("\"", ""));
      return ResModel(data: response.data.toString().replaceAll("\"", ""), success: true, status: true);
    } on DioException catch (e) {
      print(e.response?.data);
      print(jsonDecode(e.response?.data));
      return resModelFromJson(
        jsonDecode(e.response?.data) ?? getErrorFromDio(e),
      );
    } catch (e, s) {
      // Map<String, dynamic> decodedData = jsonDecode();
      print(s.toString());
      print(e.toString());
      return ResModel(message: "TTT"
          // decodedData["Message"]
          );
    }
  }

  Future getPDOIndicators() async {
    try {
      Response response = await connect().get(ApiRoutes.getPDOIndicators);
      // await storeToken(response);
      printWrapped(response.data);
      return ResModel(data: jsonDecode(response.data), status: true, success: true);
    } on DioException catch (e) {
      debugPrint('Error in req:::${e.response?.data.toString()}|\n${e.error.toString()}');
      return ResModel(status: false, message: e.error.toString());
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future getComponentsIndicators() async {
    try {
      Response response = await connect().get(ApiRoutes.getComponentsIndicators);
      printWrapped(response.data);
      return ResModel(data: jsonDecode(response.data), status: true, success: true);
    } on DioException catch (e) {
      return ResModel(status: false, message: e.error.toString());
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future requestPasswordReset({required String email}) async {
    debugPrint('requestPasswordReset');
    try {
      Response response = await connect().post(ApiRoutes.requestPasswordReset, data: {});
      printWrapped(response.data);
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future resetPassword({
    required String token,
    required String cId,
    required String password,
  }) async {
    debugPrint('resetPassword');
    try {
      Response response = await connect().post(ApiRoutes.resetPassword, data: {});
      debugPrint(response.data);
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future requestPasswordResetOTP({required String email}) async {
    debugPrint('requestPasswordResetOTP');
    try {
      Response response = await connect().post(ApiRoutes.requestPasswordResetOTP, data: {});
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future validatePasswordResetOTP({required String code}) async {
    debugPrint('validatePasswordResetOTP');
    try {
      Response response = await connect().post(ApiRoutes.validatePasswordResetOTP, data: {});
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future changePassword({required String password, required String newPassword}) async {
    debugPrint('requestPasswordReset');
    try {
      Response response = await connect().put('', data: {});
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future logout() async {
    try {
      Response response = await connect().get("");
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future verifyEmail({required VerifyEmailRequest otp}) async {
    try {
      Response response = await connect().post(ApiRoutes.verifyEmailCode, data: otp.toMap());
      debugPrint(response.data);
      // await storeToken(response);
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future forgotPassword({String? email}) async {
    try {
      Response response = await connect().post("", data: {});
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future validatePasswordOtp({required String email, required String otp, required String password}) async {
    try {
      Response response = await connect().post("", data: {});
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  Future changeTransactionPin({required String pin, required String newPin}) async {
    try {
      Response response = await connect().patch("", data: {});
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      return ResModel(message: e.toString());
    }
  }

  storeToken(
    response,
  ) async {
    // if the request failed then return;
    LoginResponse res = loginModelFromJson(response.data);
    if (res.success == false) return;

    //store token
    String _token = '${res.data?.accessToken}';
    String _tokenType = '${res.data?.tokenType}';
    String _userToken = '${res.data?.usertoken}';

    debugPrint(_token);
    await storageService.storeItem(key: bearerToken, value: _token);
    await storageService.storeItem(key: userToken, value: _userToken);
    await storageService.storeItem(key: bearerTokenType, value: _tokenType);
  }

  storeTokenOnly(
    String token,
  ) async {
    // if the request failed then return;
    debugPrint(token);
    await storageService.storeItem(key: bearerToken, value: token);
  }
}
