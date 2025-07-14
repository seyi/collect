import 'package:async/async.dart';
import 'package:dio/dio.dart';
import 'package:flutter_template/constant/constants.dart';
import 'package:flutter_template/core/services/app-settings.dart';
import 'package:flutter_template/core/services/storage-service.dart';
import 'package:flutter_template/core/services/user.service.dart';
import 'package:flutter_template/core/services/web-services/user-api-service.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/src/utils/network_exception.dart';

class Initializer {
  UserService userService = getIt<UserService>();
  StorageService storageService = getIt<StorageService>();
  UserApiService userApi = getIt<UserApiService>();

  Future initialCalls() async {
    try {
      String? value = await storageService.readItem(key: bearerToken);
      if (value != null && value.isNotEmpty) {
        await Future.wait(<Future>[
          await userService.getLocalUser(),
          userApi.getUser(),
          getUserCalls(),
        ]);
      }
    } on DioError catch (e) {
      return e.response.toString();
    } catch (e) {
      return e.toString();
    }
  }

  getUserCalls() async {
    await userService.getLocalUser();
  }

  init() async {
    try {
      getIt<AppSettingsService>().readAppSettings();
      await checkForCachedUserData();
    } catch (e) {
      rethrow;
    }
  }

  checkForCachedUserData() async {
    String? value = await storageService.readItem(key: bearerToken);
    if (value != null && value.isNotEmpty) {
      await getIt<UserService>().getLocalUser();
    }
  }

  Future getRemoteUserData() async {
    try {
      Future getUser = userApi.getUser();
      FutureGroup futureGroup = FutureGroup();

      futureGroup.add(getUser);
      futureGroup.close();
      futureGroup.future;
    } on DioError catch (e) {
      return getErrorFromDio(e);
    } catch (e) {
      return e.toString();
    }
  }
}
