import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/core/styles/text-styles.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';

import '../../core/enum/viewState.enum.dart';

class ACButton extends StatefulWidget {
  final String text;
  final Function? onPressed;
  final ViewState loadingState;
  final Color? color;
  final Color? textColor;
  final bool isFullWidth;
  final FontWeight fontWeight;
  final double fSize;

  const ACButton({
    super.key,
    required this.text,
    this.isFullWidth = true,
    this.onPressed,
    this.color,
    this.textColor,
    this.fSize = 14,
    this.fontWeight = FontWeight.w900,
    this.loadingState = ViewState.idle,
  });

  @override
  _ACButtonState createState() => _ACButtonState();
}

class _ACButtonState extends State<ACButton> {
  @override
  Widget build(BuildContext context) {
    return widget.isFullWidth
        ? SizedBox(
            // height: Platform.isAndroid ? 52.h : 46.h,
            width: double.infinity,
            child: ElevatedButton(
                onPressed: (widget.loadingState == ViewState.idle)
                    ? () {
                        try {
                          widget.onPressed!();
                        } catch (ee) {
                          print(ee.toString());
                        }
                      }
                    : () {},
                style: ElevatedButton.styleFrom(
                  backgroundColor: widget.color ?? primaryColor,
                  textStyle: AppStyles.bStyle.copyWith(
                    color: white,
                    fontWeight: widget.fontWeight,
                  ),
                  padding: EdgeInsets.symmetric(vertical: 12.r),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(32.r),
                  ),
                  elevation: 0,
                  disabledBackgroundColor: primaryColor.withOpacity(0.4),
                ),
                child: widget.loadingState == ViewState.idle
                    ? Text(
                        widget.text,
                        style: AppStyles.bStyle.copyWith(fontSize: widget.fSize.sp, fontWeight: widget.fontWeight, color: widget.textColor),
                      )
                    : const SpinKitPulse(
                        color: Colors.white,
                        size: 16.0,
                      )),
          )
        : ElevatedButton(
            onPressed: (widget.loadingState == ViewState.idle)
                ? () {
                    print('int');
                    widget.onPressed!();
                  }
                : null,
            style: ElevatedButton.styleFrom(
              backgroundColor: widget.color ?? primaryColor,
              textStyle: AppStyles.bStyle.copyWith(
                color: white,
              ),
              padding: EdgeInsets.symmetric(vertical: 17.0.h),
              shape: RoundedRectangleBorder(
                side: BorderSide(color: border),
                borderRadius: BorderRadius.circular(32.0.r),
              ),
              elevation: 0,
              disabledBackgroundColor: primaryColor.withOpacity(0.4),
            ),
            child: widget.loadingState == ViewState.idle
                ? Text(
                    widget.text,
                    style: AppStyles.bStyle.copyWith(fontSize: 14.sp, fontWeight: FontWeight.w600, color: widget.textColor),
                  )
                : const SpinKitPulse(
                    color: Colors.white,
                    size: 16.0,
                  ));
  }
}

class UiButtonOutlined extends StatefulWidget {
  final String text;
  final Function onPressed;
  final ViewState loadingState;
  final Color? outline;
  double borderRadius;
  final Color? textColor;
  UiButtonOutlined({Key? key, required this.text, required this.onPressed, this.outline, this.loadingState = ViewState.idle, this.borderRadius = 24, this.textColor}) : super(key: key);

  @override
  _UiButtonOutlinedState createState() => _UiButtonOutlinedState();
}

class _UiButtonOutlinedState extends State<UiButtonOutlined> {
  @override
  Widget build(BuildContext context) {
    return Container(
        decoration: BoxDecoration(color: white, border: Border.all(color: widget.outline ?? Color(0xFFD9EAFF)), borderRadius: BorderRadius.all(Radius.circular(widget.borderRadius))),
        child: ElevatedButton(
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.white,
            splashFactory: NoSplash.splashFactory,
            shadowColor: Colors.white,
            textStyle: TextStyle(
              color: textDark,
              fontFamily: 'BR Firma',
            ),
            // padding: const EdgeInsets.symmetric(vertical: 14.0),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(24.0),
            ),
            elevation: 0,
          ),
          onPressed: (widget.loadingState == ViewState.idle)
              ? () {
                  widget.onPressed();
                }
              : null,
          child: widget.loadingState == ViewState.idle
              ? Text(
                  widget.text,
                  style: TextStyle(fontSize: 14.sp, fontWeight: FontWeight.w500, color: widget.textColor ?? primaryDarkColor),
                )
              : SpinKitPulse(
                  color: primaryColor,
                  size: 16.0,
                ),
        ));
  }
}




class CustomButtonIcr extends StatelessWidget {
  final String text;
  final IconData icon;
  final VoidCallback onTap;
  final Color borderColor;
  final Color textColor;
  final Color iconColor;
  final double borderRadius;

  const CustomButtonIcr({
    Key? key,
    required this.text,
    required this.icon,
    required this.onTap,
    this.borderColor = Colors.blue, // Default border color
    this.textColor = Colors.black,  // Default text color
    this.iconColor = Colors.white,  // Default icon color
    this.borderRadius = 12.0,        // Default border radius
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: EdgeInsets.symmetric(vertical: 5, horizontal: 14),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(borderRadius),
          border: Border.all(color: borderColor, width: 1),
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              text,
              style: TextStyle(fontSize: 14, color: textColor),
            ),
            10.sbW,
            Container(
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: borderColor,
              ),
              padding: EdgeInsets.all(5),
              child: Icon(
                icon,
                color: iconColor,
                size: 15,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

