import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/svg.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';

import 'circular-image.dart';

class HeaderWidget extends StatelessWidget {
  final String? userName;
  final String? userPicture;
  final String greetingText;
  final ValueListenable<Object?> userNotifier;

  const HeaderWidget({
    Key? key,
    required this.userName,
    required this.userPicture,
    required this.greetingText,
    required this.userNotifier,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(top: 70.h),
      child: Row(
        mainAxisSize: MainAxisSize.max,
        children: [

          ValueListenableBuilder(
            valueListenable: userNotifier,
            builder: (context, user, _) => CircularCachedNetworkImage(
              imageUrl: userPicture ?? "",
            ),
          ),
          SizedBox(width: 12.w),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                greetingText,
                style: TextStyle(
                  color: Colors.grey, // You can customize textLight color
                  fontSize: 12.sp,
                  fontWeight: FontWeight.w400,
                ),
              ),
              SizedBox(height: 2.h),
              ValueListenableBuilder(
                valueListenable: userNotifier,
                builder: (context, user, _) => Text(
                  userName ?? "Jonah - Borno SPMU 👋",
                  style: TextStyle(
                    color: Colors.black, // You can customize primaryDarkColor here
                    fontSize: 16.sp,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
          ),
          const Spacer(),
          SvgPicture.asset('notification'.svg), // Ensure the correct path to SVG

        ],
      ),
    );
  }
}
