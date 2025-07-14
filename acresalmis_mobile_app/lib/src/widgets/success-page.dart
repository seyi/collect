import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/core/styles/text-styles.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/views/onboarding/onboarding.vm.dart';

class SuccessWidget extends StatelessWidget {
  dynamic view;
  String? body;
  bool useSubMessagePrefix;
  bool? autoImplyHome;

  SuccessWidget({
    super.key,
    this.view,
    this.body,
    this.useSubMessagePrefix = true,
    this.autoImplyHome,
  });

  @override
  Widget build(BuildContext context) {
    return BaseView<OnBoardingViewModel>(
      builder: ((context, model, child) => Scaffold(
            backgroundColor: const Color(0xff198855),
            appBar: AppBar(
              title: const Text(""),
              backgroundColor: const Color(0xff198855),
              elevation: 0,
              automaticallyImplyLeading: false,
              actions: [GestureDetector(onTap: () {
                Navigator.pop(context);
              }, child: SvgPicture.asset('close'.svg)), 18.sbW],
            ),
            body: Container(
              height: double.infinity,
              width: double.infinity,
              padding: EdgeInsets.all(16.r),
              decoration: const BoxDecoration(
                color: Color(0xff198855),
              ),
              child: Column(
                children: [
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Spacer(
                          flex: 1,
                        ),

                        ///place an animation here if any
                        // SizedBox(
                        //   width: 150.w,
                        //   child: successAnimation,
                        // ),

                        42.sbH,
                        Text(
                          "Transaction Complete",
                          style: TextStyle(fontWeight: FontWeight.w600, fontSize: 20.sp, color: white),
                        ),
                        16.sbH,
                        Container(
                          width: double.infinity,
                          padding: const EdgeInsets.all(18),
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(12),
                            gradient: const LinearGradient(
                              colors: [
                                Color(0xFF177047),
                                Color(0xFF0D5836),
                              ],
                              begin: Alignment.topLeft,
                              end: Alignment.bottomRight,
                            ),
                          ),
                          child: Column(
                            children: [
                              RichText(
                                textAlign: TextAlign.center,
                                text: TextSpan(
                                  style: TextStyle(
                                    color: AppColors.white,
                                    fontSize: 14.sp,
                                    fontWeight: FontWeight.w400,
                                  ),
                                  children: [
                                    if (useSubMessagePrefix) const TextSpan(text: "You have Successfully "),
                                    TextSpan(
                                        text: body,
                                        style: AppStyles.bStyle.copyWith(
                                          color: AppColors.white,
                                          fontSize: 14.sp,
                                          fontWeight: FontWeight.w700,
                                        )),
                                  ],
                                ),
                              )
                            ],
                          ),
                        ),
                        16.sbH,
                        24.sbH,

                        const Spacer(
                          flex: 2,
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          )),
    );
  }
}
