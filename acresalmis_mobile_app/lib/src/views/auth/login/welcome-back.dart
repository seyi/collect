import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/core/styles/text-styles.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/views/onboarding/onboarding.vm.dart';
import 'package:flutter_template/src/widgets/custom_btn.dart';
import 'package:flutter_template/src/widgets/input.dart';

class WelcomeBackAuth extends StatefulWidget {
  const WelcomeBackAuth({Key? key}) : super(key: key);

  @override
  _WelcomeBackAuthState createState() => _WelcomeBackAuthState();
}

class _WelcomeBackAuthState extends State<WelcomeBackAuth> {
  @override
  Widget build(BuildContext context) {
    return BaseView<OnBoardingViewModel>(
        // useTouchListener: false,
        builder: (context, model, child) => Scaffold(
            backgroundColor: white,
            body: Container(
              padding: EdgeInsets.symmetric(horizontal: 20.w),
              child: Column(
                children: [
                  Expanded(child: Container()),
                  32.sbH,
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Align(
                        alignment: Alignment.centerLeft,
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              "Welcome Back",
                              style: TextStyle(color: textLight, fontSize: 12.sp, fontWeight: FontWeight.w400),
                              textAlign: TextAlign.left,
                            ),
                            2.sbH,
                            Text(
                              "Linus Williams",
                              style: TextStyle(color: primaryDarkColor, fontSize: 16.w, fontWeight: FontWeight.w600),
                              textAlign: TextAlign.left,
                            ),
                          ],
                        ),
                      ),
                      GestureDetector(
                        behavior: HitTestBehavior.opaque,
                        onTap: () => Navigator.pushNamed(context, Routes.loginRoute),
                        child: Text(
                          "Login as another User",
                          style: TextStyle(color: textLight, fontSize: 10.sp, fontWeight: FontWeight.w400),
                          textAlign: TextAlign.left,
                        ),
                      ),
                    ],
                  ),
                  34.sbH,
                  const Input(
                    hintText: "Email",
                  ),
                  16.sbH,
                  Input(
                    hintText: "Password",
                    suffixIcon: Padding(
                      padding: const EdgeInsets.all(12.0),
                      child: SvgPicture.asset(
                        'eye'.svg,
                      ),
                    ),
                  ),
                  16.sbH,
                  Align(
                    alignment: Alignment.centerRight,
                    child: GestureDetector(
                      onTap: () => {},
                      child: Text(
                        "Forgot Password",
                        style: AppStyles.bStyle.copyWith(color: primaryColor, fontWeight: FontWeight.w700),
                      ),
                    ),
                  ),
                  24.sbH,
                  Row(
                    children: [
                      Expanded(
                        child: ACButton(
                          color: primaryColor,
                          onPressed: () => model.navigationService.navigateToReplace(Routes.signupRoute),
                          text: "Login",
                          loadingState: model.viewState,
                        ),
                      ),
                      16.sbW,
                      Container(
                        padding: const EdgeInsets.all(8),
                        decoration: BoxDecoration(border: Border.all(color: border), borderRadius: BorderRadius.circular(12)),
                        child: SvgPicture.asset('fingerp'.svg),
                      )
                    ],
                  ),
                  Expanded(
                    flex: 2,
                    child: Container(),
                  ),
                  Expanded(child: Container()),
                  12.sbH,
                  24.sbH,
                  GestureDetector(
                    onTap: () => model.navigationService.navigateToReplace(Routes.signupRoute),
                    child: Text(
                      "Create an Account",
                      style: AppStyles.bStyle.copyWith(color: primaryDarkColor, fontWeight: FontWeight.w600),
                    ),
                  ),
                  50.sbH
                ],
              ),
            )));
  }
}
