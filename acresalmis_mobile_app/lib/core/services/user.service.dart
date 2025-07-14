import 'package:flutter/material.dart';
import 'package:flutter_template/constant/constants.dart';
import 'package:flutter_template/core/services/app-cache.dart';
import 'package:flutter_template/core/services/storage-service.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/src/model/login-response.dart';
import 'package:flutter_template/src/model/user-response.dart';

final uService = getIt<UserService>();
final cache = getIt<AppCache>();

class UserService {
  String defaultCurrency = "GBP";
  late Locale myLocale;
  ACUser userCredentials = ACUser();
  ValueNotifier<ACUser> userCredentialsNotifier = ValueNotifier(ACUser());
  StorageService storageService = getIt<StorageService>();
  bool isVerifyingUser = false;
  bool get isLoggedIn => userCredentials.name != null;

  String? code;

  //get the user object
  getLocalUser({ACUser? user}) async {
    debugPrint("getLocalUser");
    if (user != null) {
      userCredentials = user;
      userCredentialsNotifier.value = user;
    } else {
      String? userVal = await storageService.readItem(key: currentUser);
      // print(userVal);
      if (userVal != null) {
        GetUserResponse response = userModelFromJson(userVal);
        userCredentials = response.data ?? ACUser();
        userCredentialsNotifier.value = response.data ?? ACUser();
      }
    }
  }

  //clear all user credentials
  resetAllCredentials() {
    storageService.deleteItem(key: currentUser);
    storageService.deleteItem(key: bearerToken);
    userCredentials = ACUser();
  }

  ///for password reset
  String? authProcessEmail;
  String? accountDeletionReason;
  String? userTransactionPin;
}
