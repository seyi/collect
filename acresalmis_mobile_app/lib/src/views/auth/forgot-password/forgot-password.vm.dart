import 'package:flutter/foundation.dart';
import 'package:flutter_template/base.vm.dart';
import 'package:flutter_template/core/enum/viewState.enum.dart';
import 'package:flutter_template/core/services/web-services/auth.api.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/model/login-response.dart';
import 'package:flutter_template/src/model/request/auth.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';

class ForgotPasswordViewModel extends BaseViewModel {
  final authApi = getIt<AuthenticationApiService>();
  String? email;
  String? password;
  String? confirmPassword;

  void updateEmail(String v) => update(v, (v) => email = v);
  void updatePassword(String v) => update(v, (v) => password = v);
  void updateCPassword(String v) => update(v, (v) => confirmPassword = v);

  bool get hasEmail => email.isNotNullNorEmpty;

  bool obscureText = true;
  bool obscureCText = true;

  bool get validateField => email.isNotNullNorEmpty;

  toggleObscurePassword() {
    obscureText = !obscureText;
    notifyListeners();
  }

  toggleObscureConfirmPassword() {
    obscureCText = !obscureCText;
    notifyListeners();
  }

  login() async {
    dropKeyboard();
    viewState = ViewState.busy;
    try {
      //create the  api req for signup
      LoginResponse res = await authApi.login(LoginRequest(
        email: email!.trim(),
        password: password!.trim(),
      ));
      if (res.success) {
        final user = res.data?.user;
        userService.getLocalUser(user: user);
        navigationService.navigateToReplace(Routes.homeRoute, argument: false);
        viewState = ViewState.idle;
        return;
      } else {
        viewState = ViewState.idle;
        //show error message if any
        showError(() {}, msg: res.message!);
      }
    } catch (e, stack) {
      viewState = ViewState.idle;
      //show error message if any
      debugPrint(e.toString());
      showError(() {}, msg: errorMessage);
    }
  }

  resetPassword() {
    navigationService.navigateTo(Routes.verifyOtp);
  }
}
