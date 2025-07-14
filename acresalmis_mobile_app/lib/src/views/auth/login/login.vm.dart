import 'package:flutter/foundation.dart';
import 'package:flutter_template/base.vm.dart';
import 'package:flutter_template/core/services/web-services/auth.api.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/model/request/auth.dart';
import 'package:flutter_template/src/model/res_model.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';

class LoginViewModel extends BaseViewModel {
  final authApi = getIt<AuthenticationApiService>();
  String? userName;
  String? password;

  void updateUserName(String v) => update(v, (v) => userName = v);
  void updatePassword(String v) => update(v, (v) => password = v);

  bool get hasUserName => userName.isNotNullNorEmpty;
  bool get hasPassword => password.isNotNullNorEmpty;

  bool get validateField => hasUserName && hasPassword;

  bool obscureText = true;

  toggleObscurePassword() {
    obscureText = !obscureText;
    notifyListeners();
  }

  login() async {
    dropKeyboard();
    iLoad();
    try {
      // navigationService.navigateToReplace(Routes.homeRoute, argument: false); //remove this its just for testing

      ResModel res = await authApi.login(LoginRequest(
        userName: userName!.trim(),
        password: password!.trim(),
      ));
      if (res.success!) {
        // final user = res.data?.user;
        // userService.getLocalUser(user: user);
        navigationService.navigateToReplace(Routes.homeRoute, argument: false);
        sLoad();
        return;
      } else {
        print(res.toString());
        sLoad();
        //show error message if any
        showError(() {}, msg: res.message!);
      }
    } catch (e, stack) {
      sLoad();
      //show error message if any
      debugPrint(e.toString());
      showError(() {}, msg: errorMessage);
    }
  }
}
