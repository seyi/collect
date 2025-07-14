import 'package:flutter/material.dart';
import 'package:flutter_template/constant/palette.dart';

class ShellWidget extends StatelessWidget {
  final Widget child;
  const ShellWidget({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 20),
      decoration: BoxDecoration(color: AppColors.white, border: Border.all(color: border), borderRadius: BorderRadius.circular(8)),
      child: child,
    );
  }
}
