import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_template/constant/constants.dart';
import 'package:flutter_template/core/services/navigation_service.dart';
import 'package:flutter_template/core/services/web-services/nertwork_config.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/utils/device-id-utils.dart';

import '../storage-service.dart';

Dio connect({String? useCustomUrl}) {
  final storageService = getIt<StorageService>();
  BaseOptions options = BaseOptions(
      baseUrl: useCustomUrl ?? NetworkConfig.BASE_URL, connectTimeout: const Duration(milliseconds: 350000), receiveTimeout: const Duration(milliseconds: 35000), responseType: ResponseType.plain);
  Dio dio = Dio(options);

  dio.interceptors.add(
    InterceptorsWrapper(
      onRequest: (options, handler) async {
        debugPrint(options.uri.toString());
        debugPrint(options.data.toString());
        String? id = await getUniqueDeviceId();

        ///Add device id to request
        // options.headers['device-id'] = id;

        ///log device id
        // print(id);
        String? value = await storageService.readItem(key: bearerToken);
        String? userT = await storageService.readItem(key: userToken);

        ///Add Token to Request
        if (value != null && value.isNotEmpty) {
          options.headers['Authorization'] = "Bearer $value";

          ///log token
          print(value);
          print(options.headers.toString());
        }

        ///Add Token to Request
        if (userT != null && userT.isNotEmpty) {
          options.headers['token'] = userT;

          ///log token
          print(userT);
          print(options.headers.toString());
        }

        return handler.next(options);
      },
      onResponse: (response, handler) {
        print("received response from :::${response.requestOptions.path}");
        return handler.next(response);
      },
      onError: (DioException e, handler) async {
        ///log error path
        debugPrint(
            "path: ${e.requestOptions.path}, uri: ${e.requestOptions.uri.scheme}, Error code: ${e.response?.statusCode}, Error data: ${e.response?.data}, Error response status message: ${e.response?.statusMessage}, Error message: ${e.message}, Error type name: ${e.type.name}, Error type name: ${e.type.name},   ");
        //handles auto-logout when error message contains session expiry desc.
        if (e.response?.statusCode == 401 && e.response.toString().contains("Authorization code")) {
          getIt<NavigationService>().navigateToAndRemoveUntil(Routes.loginRoute);
          getIt<StorageService>().deleteItem(key: bearerToken);
          print("Session Expired");
        }
        return handler.next(e);
      },
    ),
  );

  return dio;
}

// Dio client singleton
class ApiClient {
  static final Dio _dio = Dio(BaseOptions(
    baseUrl: NetworkConfig.BASE_URL,
    connectTimeout: const Duration(milliseconds: 350000),
    receiveTimeout: const Duration(milliseconds: 350000),
    responseType: ResponseType.plain, // Automatic JSON parsing
  ));

  static Dio get instance {
    _dio.interceptors.clear();
    _dio.interceptors.add(_mainInterceptor);
    return _dio;
  }

  static final InterceptorsWrapper _mainInterceptor = InterceptorsWrapper(
    onRequest: _onRequest,
    onResponse: _onResponse,
    onError: _onError,
  );

  static Future<void> _onRequest(RequestOptions options, handler) async {
    debugPrint('[REQ] ${options.uri}');
    final storage = getIt<StorageService>();
    // String? value,userT;
    // Parallel token fetch
    final results = await Future.wait([
      storage.readItem(key: bearerToken),
      storage.readItem(key: userToken),
    ]);
    final value = results[0] as String?;
    final userT = results[1] as String?;

    if (value?.isNotEmpty ?? false) {
      options.headers['Authorization'] = 'Bearer $value';
    }

    // if (userT?.isNotEmpty ?? false) {
    //   options.headers['token'] = userT;
    // }

    debugPrint("[HEADER:]${options.headers.toString()}");
    if (options.data != null) {
      debugPrint("[BODY:]${options.data.toString()}");
    }
    handler.next(options);
  }

  static void _onResponse(response, handler) {
    debugPrint('[RES] ${response.requestOptions.path}');
    handler.next(response);
  }

  static void _onError(DioException e, handler) {
    debugPrint('[ERR] ${e.requestOptions.uri} - ${e.message}');
    handler.next(e);
  }
}
