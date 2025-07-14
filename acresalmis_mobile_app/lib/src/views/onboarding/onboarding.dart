import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/constants.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/core/services/navigation_service.dart';
import 'package:flutter_template/core/services/storage-service.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/views/onboarding/onboarding.vm.dart';
import 'package:flutter_template/src/widgets/custom_btn.dart';
import 'package:google_fonts/google_fonts.dart';

class OnBoardingPage extends StatefulWidget {
  const OnBoardingPage({Key? key}) : super(key: key);

  @override
  State<OnBoardingPage> createState() => _OnBoardingPageState();
}

class _OnBoardingPageState extends State<OnBoardingPage> {
  int currentIndex = 0;
  late PageController _pageController;
  StorageService storageService = getIt<StorageService>();
  NavigationService navigationService = getIt<NavigationService>();
  List<OnboardModel> screens = <OnboardModel>[
    OnboardModel(
        text: "We can reduce Natural Resource Degradation",
        img: 'sp1'.png,
        title: "Our project supports strategic investments in land restoration and water conservation.",
        color: const Color(0xff2B7C44)),
    OnboardModel(
        text: "Help manage land and water for a safer, better future",
        img: 'sp2'.png,
        title: "Promote sustainable land and water management for long-term resilience.",
        color: const Color(0xff00B0F0)),
    OnboardModel(
        text: "Building Climate -Resilient Communities",
        img: 'sp3'.png,
        title: "Let's help northern Nigeria adapt to climate change and protect the environment for the future.",
        color: const Color(0xff002A55)),
  ];

  @override
  void initState() {
    _pageController = PageController(
      initialPage: 0,
    );
    super.initState();
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  _storeOnboardInfo() async {
    storageService.storeItem(key: introScreen, value: 'true');
    // await getIt<Initializer>().initOnBoardingInfo();
  }

  @override
  Widget build(BuildContext context) {
    return BaseView<OnBoardingViewModel>(
      builder: (context, model, child) => PageView.builder(
          itemCount: screens.length,
          controller: _pageController,
          // physics: const NeverScrollableScrollPhysics(),
          onPageChanged: (int index) {
            setState(() {
              currentIndex = index;
            });
          },
          itemBuilder: (_, index) {
            return Scaffold(
              backgroundColor: screens[index].color,
              appBar: AppBar(
                elevation: 0,
                backgroundColor: screens[index].color,
                automaticallyImplyLeading: false,
              ),
              body: Padding(
                padding: const EdgeInsets.symmetric(vertical: 20, horizontal: 20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Center(
                      child: Image.asset(
                        'splash_logo'.png,
                        scale: 2.0,
                      ),
                    ),
                    Expanded(flex: 2, child: Container()),
                    Center(
                      child: Image.asset(
                        screens[index].img!,
                        scale: 2,
                      ),
                    ),
                    15.sbH,
                    Padding(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 20,
                      ),
                      child: Center(
                        child: Text(
                          screens[index].text!,
                          style: GoogleFonts.manjari(color: AppColors.white, fontSize: 20.sp, fontWeight: FontWeight.w700),
                          textAlign: TextAlign.left,
                        ),
                      ),
                    ),
                    12.0.sbH,
                    Padding(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 20,
                      ),
                      child: Text(
                        screens[index].text!,
                        style: TextStyle(color: AppColors.white, fontSize: 14.sp, fontWeight: FontWeight.w400),
                        textAlign: TextAlign.left,
                      ),
                    ),
                    Expanded(flex: 2, child: Container()),
                    if (currentIndex != 2)
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Center(
                            child: Container(
                              alignment: Alignment.center,
                              height: 5.0,
                              child: ListView.builder(
                                itemCount: screens.length,
                                shrinkWrap: true,
                                scrollDirection: Axis.horizontal,
                                itemBuilder: (context, index) {
                                  return Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                                    Container(
                                      margin: const EdgeInsets.symmetric(horizontal: 3.0),
                                      width: currentIndex == index ? 32 : 12,
                                      height: currentIndex == index ? 24 : 8,
                                      decoration: BoxDecoration(
                                          color: currentIndex == index ? AppColors.white : null,
                                          borderRadius: BorderRadius.circular(10.0),
                                          border: currentIndex == index ? null : Border.all(color: AppColors.white)),
                                    ),
                                  ]);
                                },
                              ),
                            ),
                          ),
                          GestureDetector(
                            onTap: () => _pageController.animateToPage(currentIndex + 1, duration: const Duration(milliseconds: 200), curve: Curves.easeIn),
                            child: Container(
                              alignment: Alignment.center,
                              padding: const EdgeInsets.all(16),
                              decoration: BoxDecoration(shape: BoxShape.circle, border: Border.all(color: AppColors.white, width: 4)),
                              child: Icon(
                                Icons.arrow_forward_ios_rounded,
                                size: 20,
                                color: white,
                              ),
                            ),
                          ),
                        ],
                      )
                    else
                      Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 20.0),
                        child: Column(
                          children: [
                            ACButton(
                              text: "Sign In",
                              color: colorGreen,
                              onPressed: () async {
                                // _storeOnboardInfo();
                                Navigator.pushNamed(context, Routes.loginRoute);
                              },
                            ),
                            12.sbH,
                            ACButton(
                              text: "Learn More",
                              color: AppColors.white,
                              textColor: primaryColor,
                              onPressed: () async {
                                // _storeOnboardInfo();
                                Navigator.pushNamed(context, Routes.authWelcomeRoute);
                              },
                            ),
                          ],
                        ),
                      ),
                    Expanded(flex: 1, child: Container()),
                    Expanded(child: Container()),
                  ],
                ),
              ),
            );
          }),
    );
  }
}

class OnboardModel {
  String? img;
  String? text;
  String? title;
  Color? color;

  OnboardModel({this.img, this.text, this.title, this.color});
}
