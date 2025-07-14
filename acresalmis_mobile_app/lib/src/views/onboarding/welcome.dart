import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/views/onboarding/onboarding.vm.dart';
import 'package:flutter_template/src/widgets/custom_btn.dart';

class Welcome extends StatefulWidget {
  const Welcome({Key? key}) : super(key: key);

  @override
  _WelcomeState createState() => _WelcomeState();
}

class _WelcomeState extends State<Welcome> {
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
                  Hero(
                      tag: "welcomeToLogin",
                      child: Image.asset(
                        "billme-logo".png,
                      )),
                  Expanded(
                    flex: 2,
                    child: Container(),
                  ),
                  Padding(
                    padding: EdgeInsets.symmetric(horizontal: 12.0.w),
                    child: Text(
                      "Payment made easy",
                      style: TextStyle(color: primaryDarkColor, fontSize: 26.w, fontWeight: FontWeight.w600),
                      textAlign: TextAlign.center,
                    ),
                  ),
                  12.sbH,
                  Padding(
                    padding: EdgeInsets.symmetric(horizontal: 4.0.w),
                    child: Text(
                      "Say goodbye to payment hassles with BillMe! Seamlessly pay for airtime, mobile data, gift cards, and more all in one app.",
                      style: TextStyle(color: textLight, fontSize: 14.sp, fontWeight: FontWeight.w400),
                      textAlign: TextAlign.center,
                    ),
                  ),
                  Expanded(child: Container()),
                  ACButton(
                    color: primaryColor,
                    onPressed: () => model.navigationService.navigateToReplace(Routes.signupRoute),
                    text: "Signup",
                    loadingState: model.viewState,
                  ),
                  12.0.sbH,
                  ACButton(
                    color: primaryLight,
                    onPressed: () => model.navigationService.navigateToReplace(Routes.loginRoute),
                    text: "Login",
                    loadingState: model.viewState,
                  ),
                  Expanded(child: Container()),
                ],
              ),
            )));
  }
}
