import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/core/styles/text-styles.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/views/auth/login/login.vm.dart';
import 'package:flutter_template/src/widgets/custom_btn.dart';
import 'package:flutter_template/src/widgets/input.dart';
import 'package:flutter_template/src/widgets/loading-wrapper.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({Key? key}) : super(key: key);

  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  @override
  Widget build(BuildContext context) {
    return BaseView<LoginViewModel>(
        // useTouchListener: false,
        builder: (context, model, child) => LoadingWrapper(
              isLoading: model.loader,
              child: Scaffold(
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
                                "Welcome Back!",
                                style: TextStyle(color: primaryDarkColor, fontSize: 16.w, fontWeight: FontWeight.w600),
                                textAlign: TextAlign.left,
                              ),
                              2.sbH,
                              Text(
                                "Let’s resume where you left off..",
                                style: TextStyle(color: textLight, fontSize: 12.sp, fontWeight: FontWeight.w400),
                                textAlign: TextAlign.left,
                              ),
                            ],
                          ),
                        ),
                        34.sbH,
                        Input(
                          hintText: "Email",
                          onChanged: model.updateUserName,
                          keyboardType: TextInputType.emailAddress,
                        ),
                        16.sbH,
                        Input(
                          hintText: "Password",
                          onChanged: model.updatePassword,
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
                        24.sbH,
                        ACButton(
                          color: primaryColor,
                          onPressed: () => model.validateField ? model.login() : model.showError(() {}, msg: "Please fill all Fields"),
                          text: "Sign In",
                          loadingState: model.viewState,
                        ),
                        16.sbH,
                        GestureDetector(
                          onTap: () => Navigator.pushNamed(context, Routes.forgotPasswordRoute),
                          child: Align(
                            alignment: Alignment.centerRight,
                            child: RichText(
                              textAlign: TextAlign.left,
                              text: TextSpan(
                                style: AppStyles.bStyle.copyWith(
                                  fontSize: 14.0,
                                  color: nTextLight,
                                ),
                                children: [
                                  TextSpan(
                                    text: 'You forgot your ',
                                    style: TextStyle(color: nTextLight, fontSize: 14),
                                  ),
                                  TextSpan(
                                    text: 'Password?',
                                    style: TextStyle(color: greenDark, fontWeight: FontWeight.w700),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ),
                        Expanded(
                          flex: 2,
                          child: Container(),
                        ),
                        Expanded(child: Container()),
                        24.sbH,
                        50.sbH
                      ],
                    ),
                  )),
            ));
  }
}
