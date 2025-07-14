import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/core/styles/text-styles.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/views/auth/signup/signup.vm.dart';
import 'package:flutter_template/src/widgets/custom_btn.dart';
import 'package:flutter_template/src/widgets/otp-entry.dart';
import 'package:flutter_template/src/widgets/resend-otp-widget.dart';

class VerifyOtpPage extends StatefulWidget {
  const VerifyOtpPage({Key? key}) : super(key: key);

  @override
  _VerifyOtpPageState createState() => _VerifyOtpPageState();
}

class _VerifyOtpPageState extends State<VerifyOtpPage> {
  final ValueNotifier<bool> isShowingTimer = ValueNotifier(false);
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
                        Image.asset(
                          'logo'.png,
                          scale: 2.0,
                        ),
                        Text(
                          "Let's Verify you",
                          style: TextStyle(color: primaryDarkColor, fontSize: 16.w, fontWeight: FontWeight.w600),
                          textAlign: TextAlign.left,
                        ),
                        2.sbH,
                        Text(
                          "Enter OTP sent to acresal@gmail.com",
                          style: TextStyle(color: textLight, fontSize: 12.sp, fontWeight: FontWeight.w400),
                          textAlign: TextAlign.left,
                        ),
                      ],
                    ),
                  ),
                  34.sbH,
                  OtpEntryField(
                    length: 5,
                    onComplete: (otp) {
                      print('OTP entered: $otp');
                      model.updateOtp(otp);
                      model.verifyOtpUser();
                    },
                  ),
                  16.sbH,
                  12.sbH,
                  ACButton(
                    color: primaryColor,
                    onPressed: () => model.navigationService.navigateToReplace(Routes.resetPassword),
                    text: "Verify",
                    loadingState: model.viewState,
                  ),
                  24.sbH,
                  ResendOtpWidget(
                    isShowingTimer: isShowingTimer,
                    resendOtpCallback: () => {},
                    customWidget: Align(
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
                              text: "Didn't get an otp? ",
                              style: TextStyle(color: nTextLight, fontSize: 14),
                            ),
                            TextSpan(
                              text: 'Resend OTP',
                              style: TextStyle(color: greenDark, fontWeight: FontWeight.w700),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                  Expanded(
                    flex: 3,
                    child: Container(),
                  ),
                  50.sbH
                ],
              ),
            )));
  }
}
