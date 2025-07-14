import 'package:flutter_template/src/utils/string-extensions.dart';

class NavigationModel {
  const NavigationModel(this.index, this.title, this.icon, this.activeIcon);
  final int index;
  final String title;
  final String icon;
  final String activeIcon;
}

List<NavigationModel> navigationModel = <NavigationModel>[
   NavigationModel(0, 'Home', "home".svg, "home".svg),
  NavigationModel(1, 'Activities', "activities".svg, "activities".svg),
  NavigationModel(2, 'More', "more".svg, "more".svg),
  NavigationModel(3, 'Account', "account".svg, "account".svg),
];
