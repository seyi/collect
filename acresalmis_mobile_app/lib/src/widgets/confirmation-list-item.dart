import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/core/styles/text-styles.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';

class CListItem extends StatelessWidget {
  final String title;
  final String value;
  final bool formatAmount;
  const CListItem({
    super.key,
    required this.title,
    this.formatAmount = false,
    required this.value,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(12),
        color: AppColors.white,
        border: Border.all(color: const Color(0xffE0F6FF)),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            title,
            style: AppStyles.bStyle.copyWith(color: textLight, fontSize: 12),
          ),
          32.sbW,
          Flexible(
            child: Text(
              textAlign: TextAlign.right,
              formatAmount ? formatPrice(value) : value,
              style: AppStyles.bStyle.copyWith(color: textDark, fontWeight: FontWeight.w700, fontSize: 14.sp),
            ),
          ),
        ],
      ),
    );
  }
}
