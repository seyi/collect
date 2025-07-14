import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_template/core/services/app-cache.dart';
import 'package:flutter_template/core/services/web-services/base-api.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/src/model/res_model.dart';
import 'package:flutter_template/src/utils/network_exception.dart';

class AppApiService {
  final cache = getIt<AppCache>();

  Future getAdminSettings() async {
    debugPrint("getAdminSettings");
    try {
      Response response = await connect().get("");
      if (response.statusCode == 200 || response.statusCode == 201) {}
      return resModelFromJson(response.data);
    } on DioException catch (e) {
      debugPrint('Failed to admin settings');
      return resModelFromJson(e.response?.data ?? getErrorFromDio(e));
    } catch (e) {
      debugPrint(e.toString());
      return ResModel(message: e.toString());
    }
  }
}
