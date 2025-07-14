import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/src/views/home/homepage.dart';
import 'package:flutter_template/src/views/home/nav_model.dart';
import 'package:flutter_template/src/views/onboarding/onboarding.vm.dart';
import 'package:flutter_template/src/views/profile/profille.ui.dart';

class BottomNavPage extends StatefulWidget {
  const BottomNavPage({Key? key}) : super(key: key);

  @override
  _BottomNavPageState createState() => _BottomNavPageState();
}

class _BottomNavPageState extends State<BottomNavPage> {
  @override
  Widget build(BuildContext context) {
    return BaseView<OnBoardingViewModel>(
        builder: (context, model, child) => Scaffold(
              resizeToAvoidBottomInset: false,
              backgroundColor: lightBlueBg,
              bottomNavigationBar: BottomAppBar(
                elevation: 0,
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
                  decoration: BoxDecoration(
                    color: Colors.white, // Ensure a visible background color
                    borderRadius: BorderRadius.circular(30), // Small border radius
                    // boxShadow: [
                    //   // Optional: Add shadow for better visibility
                    //   BoxShadow(
                    //     color: Colors.grey.withOpacity(0.5),
                    //     blurRadius: 4,
                    //     offset: Offset(0, 2),
                    //   ),
                    // ],
                  ),
                  // clipBehavior: Cli
                  // p.hardEdge,
                  // Clip overflowing children
                  child: Row(
                    mainAxisSize: MainAxisSize.max,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: navigationModel
                        .map(
                          (e) => InkWell(
                            highlightColor: Colors.transparent,
                            splashFactory: NoSplash.splashFactory,
                            child: AnimatedContainer(
                              duration: const Duration(milliseconds: 200),
                              padding: e.index == model.selectedIndex ? const EdgeInsets.symmetric(horizontal: 12, vertical: 4) : EdgeInsets.zero,
                              child: Column(
                                mainAxisSize: MainAxisSize.min,
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  SvgPicture.asset(
                                    e.icon,
                                    color: e.index == model.selectedIndex ? greenDark : textLight,
                                  ),
                                  SizedBox(height: 3), // Replace 3.0.sbH
                                  Text(
                                    e.title,
                                    style: TextStyle(
                                      color: e.index == model.selectedIndex ? greenDark : textLight,
                                      fontSize: 12,
                                      fontWeight: FontWeight.bold,
                                    ),
                                  ),
                                ],
                              ),
                            ),
                            onTap: () => model.onItemTapped(e.index),
                          ),
                        )
                        .toList(),
                  ),
                ),
              ),
              body: Container(
                child: <Widget>[const HomePage(), const HomePage(), const HomePage(), const ProfilePage()].elementAt(model.selectedIndex),
              ),
            ));
  }
}
