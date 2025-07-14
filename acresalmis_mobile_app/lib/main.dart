import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/core/services/navigation_service.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/routes/router.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:oktoast/oktoast.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await dotenv.load(fileName: ".env");
  await dependenciesInjectorSetup();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  final bool? loggedIn;

  const MyApp({
    Key? key,
    this.loggedIn,
  }) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return OKToast(
      child: ScreenUtilInit(
        useInheritedMediaQuery: true,
        designSize: const Size(390, 844),
        minTextAdapt: true,
        splitScreenMode: true,
        builder: (child, _) {
          return GestureDetector(
            onTap: () {
              final currentFocus = FocusScope.of(context);
              if (!currentFocus.hasPrimaryFocus && currentFocus.focusedChild != null) {
                FocusManager.instance.primaryFocus?.unfocus();
              }
            },
            child: MaterialApp(
                debugShowCheckedModeBanner: false,
                navigatorKey: getIt<NavigationService>().navigatorKey,
                scaffoldMessengerKey: getIt<NavigationService>().snackBarKey,
                theme: ThemeData(
                  useMaterial3: false,
                  scaffoldBackgroundColor: white,
                  textTheme: Theme.of(context).textTheme.apply(bodyColor: primaryDarkColor, displayColor: primaryDarkColor, fontFamily: GoogleFonts.poppins().fontFamily),
                  primaryColor: primaryColor,
                  visualDensity: VisualDensity.adaptivePlatformDensity,
                  colorScheme: ThemeData().colorScheme.copyWith(primary: primaryColor).copyWith(surface: white),
                ),
                onGenerateRoute: Routers.generateRoute,
                initialRoute: Routes.splashRoute,
                // getIt<UserService>().isLoggedIn ? Routes.loginRoute : Routes.welcomeRoute,

                // here
                builder: (context, widget) {
                  return MediaQuery(
                    //Setting font does not change with system font size
                    data: MediaQuery.of(context).copyWith(textScaler: const TextScaler.linear(1.0)),
                    child: ScrollConfiguration(
                      behavior: const ScrollBehavior(),
                      child: widget!,
                    ),
                  );
                }),
          );
        },
      ),
    );
  }
}
