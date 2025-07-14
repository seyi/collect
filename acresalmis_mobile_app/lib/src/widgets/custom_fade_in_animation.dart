import 'package:animation_wrappers/animation_wrappers.dart';
import 'package:flutter/material.dart';

class CustomFadeIn extends StatelessWidget {
  final Widget child;
  const CustomFadeIn({Key? key, required this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return FadedSlideAnimation(
      child: child,
      beginOffset: Offset(0, 0.5),
      endOffset: Offset(0, 0),
      slideCurve: Curves.linearToEaseOut,
    );
  }
}
