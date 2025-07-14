import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/views/auth/forgot-password/forgot-password.vm.dart';
import 'package:flutter_template/src/widgets/custom_btn.dart';
import 'package:flutter_template/src/widgets/input.dart';
import 'package:flutter_template/src/widgets/password-reset-success.dart';

class ResetPassword extends StatefulWidget {
  const ResetPassword({Key? key}) : super(key: key);

  @override
  _ResetPasswordState createState() => _ResetPasswordState();
}

class _ResetPasswordState extends State<ResetPassword> {
  @override
  Widget build(BuildContext context) {
    return BaseView<ForgotPasswordViewModel>(
        // useTouchListener: false,
        builder: (context, model, child) => Scaffold(
            backgroundColor: white,
            body: Container(
              padding: EdgeInsets.symmetric(horizontal: 20.w),
              child: Column(
                children: [
                  Expanded(child: Container()),
                  32.sbH,
                  Align(
                    alignment: Alignment.centerLeft,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Image.asset(
                          'logo'.png,
                          scale: 2.0,
                        ),
                        Text(
                          "Choose a Password",
                          style: TextStyle(color: primaryDarkColor, fontSize: 16.w, fontWeight: FontWeight.w600),
                          textAlign: TextAlign.left,
                        ),
                        2.sbH,
                        Text(
                          "Enter a safe password for your protection",
                          style: TextStyle(color: textLight, fontSize: 12.sp, fontWeight: FontWeight.w400),
                          textAlign: TextAlign.left,
                        ),
                      ],
                    ),
                  ),
                  34.sbH,
                  Input(
                    hintText: "Password",
                    onChanged: (v) => model.updatePassword(v),
                    keyboardType: TextInputType.visiblePassword,
                    obscureText: model.obscureText,
                    suffixIcon: GestureDetector(
                      onTap: model.toggleObscurePassword,
                      child: Padding(
                        padding: const EdgeInsets.all(12.0),
                        child: SvgPicture.asset(
                          'eyeslash'.svg,
                        ),
                      ),
                    ),
                  ),
                  16.sbH,
                  Input(
                    hintText: "Confirm Password",
                    onChanged: (v) => model.updateCPassword(v),
                    keyboardType: TextInputType.visiblePassword,
                    obscureText: model.obscureCText,
                    suffixIcon: GestureDetector(
                      onTap: model.toggleObscureConfirmPassword,
                      child: Padding(
                        padding: const EdgeInsets.all(12.0),
                        child: SvgPicture.asset(
                          'eyeslash'.svg,
                        ),
                      ),
                    ),
                  ),
                  16.sbH,
                  24.sbH,
                  ACButton(
                    color: primaryColor,
                    onPressed: () => showPasswordSuccessSheet(context, () {
                      Navigator.pop(context);
                      Navigator.pushNamed(context, Routes.loginRoute);
                    }),
                    // model.validateField ? model.login() : model.showError(() {}, msg: "Please fill all Fields"),
                    text: "Update Password",
                    loadingState: model.viewState,
                  ),
                  16.sbH,
                  Expanded(
                    flex: 2,
                    child: Container(),
                  ),
                  Expanded(child: Container()),

                  24.sbH,
                  // GestureDetector(
                  //   onTap: () => model.navigationService.navigateToReplace(Routes.authWelcomeRoute),
                  //   child: Text(
                  //     "Create an Account",
                  //     style: AppStyles.bStyle.copyWith(color: primaryDarkColor, fontWeight: FontWeight.w600),
                  //   ),
                  // ),
                  50.sbH
                ],
              ),
            )));
  }
}
