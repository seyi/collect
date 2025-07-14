import 'dart:core';

import 'package:flutter/material.dart';
import 'package:flutter_template/core/services/app-cache.dart';
import 'package:flutter_template/core/services/storage-service.dart';
import 'package:flutter_template/core/services/user.service.dart';
import 'package:flutter_template/core/services/web-services/app-api-service.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/src/utils/debounce.dart';

import '../core/enum/viewState.enum.dart';
import '../core/services/navigation_service.dart';

class BaseViewModel extends ChangeNotifier {
  final debouncer = Debouncer();
  ViewState _viewState = ViewState.idle;
  NavigationService navigationService = getIt<NavigationService>();
  UserService userService = getIt<UserService>();
  final appApi = getIt<AppApiService>();
  final cache = getIt<AppCache>();

  StorageService storageService = getIt<StorageService>();
  AppCache appCache = getIt<AppCache>();

  ViewState get viewState => _viewState;

  set viewState(ViewState newState) {
    if (newState == ViewState.busy) {
      hasError = false;
    }
    _viewState = newState;
    notifyListeners();
  }

  bool isLoading = false;
  bool loader = false;

  bool hasError = false;
  Function? retryFunction;
  String? errorMessage;

  void showError(Function function, {String? msg}) {
    if (!hasError) {
      hasError = true;
      retryFunction = function;
      errorMessage = msg ?? "Request Failed, Try again";
      notifyListeners();
      stopLoader();
    }
  }

  clearError() {
    hasError = false;
    errorMessage = null;
    // if (retryFunction != null) closeFunction!();
    notifyListeners();
  }

  void iLoad() {
    if (!loader) {
      hasError = false;
      loader = true;
      viewState = ViewState.busy;
      notifyListeners();
    }
  }

  void sLoad() {
    if (loader) {
      loader = false;
      viewState = ViewState.idle;
      notifyListeners();
    }
  }

  void startLoader() {
    if (!isLoading) {
      hasError = false;
      isLoading = true;
      viewState = ViewState.busy;
      notifyListeners();
    }
  }

  void stopLoader() {
    if (isLoading) {
      isLoading = false;
      viewState = ViewState.idle;
      notifyListeners();
    }
  }

  dropKeyboard() {
    FocusManager.instance.primaryFocus?.unfocus();
  }
}

extension AutoNotifyExtension on ChangeNotifier {
  void update<T>(T value, T Function(T) setter) {
    setter(value);
    notifyListeners();
  }
}
