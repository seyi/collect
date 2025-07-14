import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/constants.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/core/styles/text-styles.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/views/auth/signup/signup.vm.dart';
import 'package:flutter_template/src/widgets/custom_btn.dart';
import 'package:flutter_template/src/widgets/input.dart';

class SignUpPage extends StatefulWidget {
  const SignUpPage({Key? key}) : super(key: key);

  @override
  _SignUpPageState createState() => _SignUpPageState();
}

class _SignUpPageState extends State<SignUpPage> {
  @override
  Widget build(BuildContext context) {
    return BaseView<SignUpViewModel>(
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
                        Text(
                          "Welcome to BillMe",
                          style: TextStyle(color: primaryDarkColor, fontSize: 16.w, fontWeight: FontWeight.w600),
                          textAlign: TextAlign.left,
                        ),
                        2.sbH,
                        Text(
                          "Create an account to start your BillMe journey",
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
                  Row(
                    children: [
                      Expanded(
                          child: Input(
                        hintText: "First Name",
                        onChanged: (v) => model.updateFName(v),
                        keyboardType: TextInputType.name,
                      )),
                      16.sbW,
                      Expanded(
                          child: Input(
                        hintText: "Last Name",
                        onChanged: (v) => model.updateLName(v),
                        keyboardType: TextInputType.name,
                      ))
                    ],
                  ),
                  16.sbH,
                  Input(
                    hintText: "Phone Number",
                    onChanged: (v) => model.updatePhone(v),
                    keyboardType: TextInputType.phone,
                    inputFormatters: numberFilter,
                  ),
                  16.sbH,
                  Input(
                    hintText: "Password",
                    onChanged: (v) => model.updatePassword(v),
                    keyboardType: TextInputType.visiblePassword,
                    suffixIcon: Padding(
                      padding: const EdgeInsets.all(12.0),
                      child: SvgPicture.asset(
                        'eye'.svg,
                      ),
                    ),
                  ),
                  16.sbH,
                  Expanded(
                    flex: 2,
                    child: Container(),
                  ),
                  Expanded(child: Container()),
                  RichText(
                    textAlign: TextAlign.left,
                    text: TextSpan(
                      style: AppStyles.bStyle.copyWith(
                        fontSize: 12.0,
                        color: Colors.black,
                      ),
                      children: [
                        TextSpan(
                          text: 'By signing you agree to the ',
                          style: TextStyle(color: textLight),
                        ),
                        TextSpan(
                          text: 'term and condition',
                          style: TextStyle(color: primaryColor, fontWeight: FontWeight.w700),
                        ),
                      ],
                    ),
                  ),
                  12.sbH,
                  ACButton(
                    color: primaryColor,
                    onPressed: () => model.navigationService.navigateToReplace(Routes.verifyOtp),
                    text: "Signup",
                    loadingState: model.viewState,
                  ),
                  24.sbH,
                  GestureDetector(
                    onTap: () => model.navigationService.navigateToReplace(Routes.loginRoute),
                    child: Text(
                      "I already have an account",
                      style: AppStyles.bStyle.copyWith(color: primaryDarkColor, fontWeight: FontWeight.w600),
                    ),
                  ),
                  50.sbH
                ],
              ),
            )));
  }
}
