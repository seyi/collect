import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/views/auth/forgot-password/forgot-password.vm.dart';
import 'package:flutter_template/src/widgets/custom_btn.dart';
import 'package:flutter_template/src/widgets/input.dart';

class ForgotPassword extends StatefulWidget {
  const ForgotPassword({Key? key}) : super(key: key);

  @override
  _ForgotPasswordState createState() => _ForgotPasswordState();
}

class _ForgotPasswordState extends State<ForgotPassword> {
  @override
  Widget build(BuildContext context) {
    return BaseView<ForgotPasswordViewModel>(
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
                        Image.asset('logo'.png, scale: 2.0),
                        Text(
                          "Forgot Password",
                          style: TextStyle(color: primaryDarkColor, fontSize: 16.w, fontWeight: FontWeight.w600),
                          textAlign: TextAlign.left,
                        ),
                        2.sbH,
                        Text(
                          "Enter your email below to reset password",
                          style: TextStyle(color: textLight, fontSize: 12.sp, fontWeight: FontWeight.w400),
                          textAlign: TextAlign.left,
                        ),
                      ],
                    ),
                  ),
                  34.sbH,
                  Input(
                    hintText: "Email",
                    onChanged: (v) => model.updateEmail(v),
                    keyboardType: TextInputType.emailAddress,
                  ),
                  16.sbH,
                  ACButton(
                    color: primaryColor,
                    onPressed: () => model.hasEmail ? model.resetPassword() : model.showError(() {}, msg: "Please fill all Fields"),
                    text: "Continue",
                    loadingState: model.viewState,
                  ),
                  16.sbH,
                  // GestureDetector(
                  //   onTap: () => {},
                  //   child: Align(
                  //     alignment: Alignment.centerRight,
                  //     child: RichText(
                  //       textAlign: TextAlign.left,
                  //       text: TextSpan(
                  //         style: AppStyles.bStyle.copyWith(
                  //           fontSize: 14.0,
                  //           color: nTextLight,
                  //         ),
                  //         children: [
                  //           TextSpan(
                  //             text: 'You forgot your ',
                  //             style: TextStyle(color: nTextLight, fontSize: 14),
                  //           ),
                  //           TextSpan(
                  //             text: 'Password?',
                  //             style: TextStyle(color: greenDark, fontWeight: FontWeight.w700),
                  //           ),
                  //         ],
                  //       ),
                  //     ),
                  //   ),
                  // ),
                  Expanded(
                    flex: 2,
                    child: Container(),
                  ),
                  Expanded(child: Container()),
                  24.sbH,
                  50.sbH
                ],
              ),
            )));
  }
}
//D4EDD6
