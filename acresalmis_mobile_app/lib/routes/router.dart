import 'package:flutter/material.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/model/component-model.dart';
import 'package:flutter_template/src/views/auth/forgot-password/forgot-password.ui.dart';
import 'package:flutter_template/src/views/auth/forgot-password/reset-password.dart';
import 'package:flutter_template/src/views/auth/login/login.dart';
import 'package:flutter_template/src/views/auth/login/welcome-back.dart';
import 'package:flutter_template/src/views/auth/signup/signup.dart';
import 'package:flutter_template/src/views/auth/signup/verify-otp.dart';
import 'package:flutter_template/src/views/components/component-a/activity-info.ui.dart';
import 'package:flutter_template/src/views/components/component-a/beneficiary.ui.dart';
import 'package:flutter_template/src/views/components/component-a/catchment-info.ui.dart';
import 'package:flutter_template/src/views/components/select-sub-component.ui.dart';
import 'package:flutter_template/src/views/components/sub-component-dashboard.dart';
import 'package:flutter_template/src/views/home/bottomnav.dart';
import 'package:flutter_template/src/views/onboarding/onboarding.dart';
import 'package:flutter_template/src/views/onboarding/splash.dart';
import 'package:flutter_template/src/views/onboarding/welcome.dart';
import 'package:flutter_template/src/widgets/polygon.dart';
import 'package:flutter_template/src/widgets/success-page.dart';

import '../src/views/components/component-a/activity-sub-component-info.ui.dart';
import '../src/views/components/component-a/select-result-framework.ui.dart';

class Routers {
  static Route<dynamic> generateRoute(RouteSettings settings) {
    final args = settings.arguments;
    switch (settings.name) {
      case Routes.homeRoute:
        return MaterialPageRoute(builder: (_) => const BottomNavPage());
      case Routes.loginRoute:
        return MaterialPageRoute(builder: (_) => const LoginPage());
      case Routes.verifyOtp:
        return MaterialPageRoute(builder: (_) => const VerifyOtpPage());
      case Routes.welcomeRoute:
        return MaterialPageRoute(builder: (_) => const Welcome());

      case Routes.authWelcomeRoute:
        return MaterialPageRoute(builder: (_) => const WelcomeBackAuth());
      case Routes.signupRoute:
        return MaterialPageRoute(builder: (_) => const SignUpPage());
      case Routes.splashRoute:
        return MaterialPageRoute(builder: (_) => const SplashScreen());
      case Routes.onboardingPageRoute:
        return MaterialPageRoute(builder: (_) => const OnBoardingPage());
      case Routes.forgotPasswordRoute:
        return MaterialPageRoute(builder: (_) => const ForgotPassword());
      case Routes.resetPassword:
        return MaterialPageRoute(builder: (_) => const ResetPassword());
      case Routes.selectSubComponentRoute:
        return MaterialPageRoute(
            builder: (_) => SelectSubComponentPage(
                  component: args as ComponentEnum,
                ));
      case Routes.subComponentDashboardRoute:
        return MaterialPageRoute(builder: (_) => SubComponentDashboardPage(componentModel: args as ComponentModel));
      case Routes.catchmentInfoRoute:
        return MaterialPageRoute(builder: (_) => const CatchmentInfoPage());
      case Routes.activitiesInfoRoute:
        return MaterialPageRoute(builder: (_) => const ActivitiesInfoPage());
      case Routes.activitySubComponentInfoRoute:
        return MaterialPageRoute(builder: (_) => const ActivitySubComponentInfoPage());
      case Routes.selectResultFrameworkRoute:
        return MaterialPageRoute(builder: (_) => const SelectResultFrameworkPage());

      case Routes.beneficiaryInfoRoute:
        return MaterialPageRoute(builder: (_) => const BeneficiaryInfoPage());
      case Routes.polyPage:
        return MaterialPageRoute(builder: (_) => PolygonDrawingMap());

      case Routes.successPage:
        Map extras = args as Map;
        return MaterialPageRoute(
            builder: (_) => SuccessWidget(
                  body: extras['body'] ?? "",
                  useSubMessagePrefix: extras['useSubMessagePrefix'] ?? true,
                  view: extras['view'],
                  autoImplyHome: extras['autoImplyHome'] ?? true,
                ));

      default:
        return MaterialPageRoute(builder: (_) => const LoginPage());
    }
  }
}
