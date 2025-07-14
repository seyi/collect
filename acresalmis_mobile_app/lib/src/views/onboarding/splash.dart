import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/core/services/notification-handler.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/views/onboarding/onboarding.vm.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({Key? key}) : super(key: key);

  @override
  _SplashScreenState createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> with TickerProviderStateMixin {
  late final AnimationController _animationController;
  late AnimationController _controller;
  late Animation<double> _animation;
  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    );
    _animation = Tween<double>(begin: 0, end: 1).animate(_controller);

    // Start the animation when the widget is inserted into the tree
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _controller.forward();
    });

    // Add a listener to the controller to detect when the animation is complete
    _controller.addStatusListener((status) {
      if (status == AnimationStatus.completed) {
        continueAfterSplash();
        ;
      }
    });
  }

  void continueAfterSplash() {
    Navigator.pushReplacementNamed(context, isFirstLaunch.isNotNullNorEmpty ? Routes.loginRoute : Routes.onboardingPageRoute);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return BaseView<OnBoardingViewModel>(
        builder: (context, model, child) => Scaffold(
            backgroundColor: white,
            body: Container(
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                  colors: [
                    Color(0xFF3E7A46), // Lightest green
                    Color(0xFF264229),
                    Colors.black // Darkest green
                  ],
                ),
              ),
              padding: EdgeInsets.symmetric(horizontal: 20.w),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Center(
                    child: ClipRect(
                      child: AnimatedBuilder(
                        animation: _animation,
                        builder: (context, child) {
                          return Align(
                            alignment: Alignment.centerLeft,
                            widthFactor: _animation.value,
                            child: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Image.asset(
                                  'splash_logo'.png,
                                  scale: 2.0,
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                    ),
                  )
                ],
              ),
            )));
  }
}
