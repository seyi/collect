import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_template/core/styles/text-styles.dart';

class SmallUIButton extends StatelessWidget {
  Color? borderColor;
  Color? bgColor;
  String? title;
  Color? textColor;
  Function? onTap;
  Widget? titleWidget;
  EdgeInsetsGeometry? padding;
  SmallUIButton({Key? key, this.title, this.bgColor, this.borderColor, this.titleWidget, this.textColor, this.padding, this.onTap}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return TextButton(
        style: TextButton.styleFrom(
          shape: RoundedRectangleBorder(side: borderColor != null ? BorderSide(color: borderColor!) : BorderSide(color: bgColor!), borderRadius: BorderRadius.circular(12.0)),
          backgroundColor: bgColor,
          padding: padding ?? EdgeInsets.symmetric(horizontal: 11.w, vertical: 16.h),
        ),
        onPressed: () => onTap!() ?? {},
        child: titleWidget ??
            Text(
              title ?? "",
              style: AppStyles.bStyle.copyWith(color: textColor, fontWeight: FontWeight.w700, fontSize: 16),
            ));
  }
}
