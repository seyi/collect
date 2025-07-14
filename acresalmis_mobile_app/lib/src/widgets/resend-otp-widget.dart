import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:timer_count_down/timer_count_down.dart';

class ResendOtpWidget extends StatelessWidget {
  final int seconds;
  final Widget? customWidget;
  final Function resendOtpCallback;

  const ResendOtpWidget({Key? key, required this.isShowingTimer, this.seconds = 20, this.customWidget, required this.resendOtpCallback}) : super(key: key);

  final ValueNotifier<bool> isShowingTimer;

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder(
        valueListenable: isShowingTimer,
        builder: (context, bool showTimer, Widget? child) => showTimer
            ? Center(
                child: Countdown(
                seconds: seconds,
                build: (BuildContext context, double time) => Text(
                  "00:${time.toString().split(".")[0]}",
                  style: TextStyle(color: primaryColor, fontWeight: FontWeight.bold, fontSize: 14),
                ),
                interval: const Duration(milliseconds: 100),
                onFinished: () {
                  isShowingTimer.value = !isShowingTimer.value;
                },
              ))
            : Container(
                alignment: Alignment.topRight,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    customWidget == null
                        ? Text(
                            'I have not received an OTP. ',
                            style: TextStyle(color: primaryDarkColor, fontFamily: 'Inter', fontWeight: FontWeight.w400, fontSize: 12.sp),
                          )
                        : const SizedBox.shrink(),
                    GestureDetector(
                      onTap: () {
                        /*TODO: Add resend otp callback from view model
                          model.resendOtp();
                        */
                        resendOtpCallback();
                        isShowingTimer.value = !isShowingTimer.value;
                      },
                      child: customWidget ??
                          Text(
                            'Resend',
                            style: TextStyle(color: primaryDarkColor, fontFamily: 'Inter', fontWeight: FontWeight.w600, fontSize: 12.sp),
                          ),
                    ),
                  ],
                ),
              ));
  }
}
