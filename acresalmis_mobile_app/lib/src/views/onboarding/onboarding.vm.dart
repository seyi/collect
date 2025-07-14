import 'package:flutter_template/base.vm.dart';

class OnBoardingViewModel extends BaseViewModel {
  int selectedIndex = 0;
  void onItemTapped(int index) {
    selectedIndex = index;
    notifyListeners();
  }
}
