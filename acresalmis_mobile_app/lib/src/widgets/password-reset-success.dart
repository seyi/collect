import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/core/styles/text-styles.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/widgets/appbar.dart';
import 'package:flutter_template/src/widgets/custom_btn.dart';

class PasswordSuccess extends StatelessWidget {
  final Function() onDone;

  const PasswordSuccess({
    super.key,
    required this.onDone,
  });
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: acAppBar('', bgColor: Color(0xFFD4EDD6).withOpacity(0.37), showBackButton: false),
        backgroundColor: const Color(0xFFD4EDD6).withOpacity(0.37),
        body: Container(
          padding: const EdgeInsets.all(20),
          child: Column(
            children: [
              24.sbH,
              Image.asset(
                'logo'.png,
                scale: 2.0,
              ),
              Expanded(child: Container()),
              SvgPicture.asset('success-check'.svg),
              Expanded(child: Container()),
              Text(
                "Awesome",
                style: AppStyles.bStyle.copyWith(color: primaryColor, fontWeight: FontWeight.w700, fontSize: 32),
              ),
              12.sbH,
              Text(
                "You have successfully set your new password",
                style: AppStyles.bStyle.copyWith(color: nTextLight, fontWeight: FontWeight.w500, fontSize: 18),
                textAlign: TextAlign.center,
              ),
              Expanded(child: Container()),
              ACButton(
                text: "Continue to Login",
                color: primaryColor,
                onPressed: () => onDone(),
              ),
              Expanded(child: Container()),
            ],
          ),
        ));
  }
}

showPasswordSuccessSheet(
  BuildContext context,
  Function() onDone,
) {
  showModalBottomSheet(
      enableDrag: true,
      isDismissible: true,
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.white,
      builder: (context) {
        return PasswordSuccess(onDone: () => onDone());
      });
}
