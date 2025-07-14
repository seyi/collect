import 'package:flutter/material.dart';
import 'package:flutter_template/base.vm.dart';
import 'package:flutter_template/core/services/web-services/components.api.dart';
import 'package:flutter_template/core/services/web-services/user-api-service.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/src/model/component-indicator-response.dart';
import 'package:flutter_template/src/model/pdo-response.dart';
import 'package:flutter_template/src/model/res_model.dart';

import '../../../constant/palette.dart';

class HomeViewModel extends BaseViewModel {
  final userApi = getIt<UserApiService>();

  Map<String, double> dataMap = {
    "Component A": 5,
    "Component B": 3,
    "Component C": 2,
    "Component D": 2,
  };

  final List<Color> colorList = [primaryColor, greenDark, secondaryColor, colorGreen];

  HomeViewModel() {
    refresh();
  }
  getComponentsIndicator() async {
    dropKeyboard();
    iLoad();
    try {
      ResModel res = await getIt<ComponentsApiService>().getComponentsIndicators();
      if (res.success!) {
        sLoad();
        if (res.data != null) {
          // Check if data is a List
          if (res.data is List) {
            (res.data as List).forEach((e) {
              if (e != null) {
                appCache.componentIndicators?.add(ComponentIndicator.fromJson(e));
              }
            });
          }
          // If data is a Map, extract the actual list (adjust key as needed)
          else if (res.data is Map && res.data.containsKey('results')) {
            (res.data['results'] as List).forEach((e) {
              if (e != null) {
                appCache.componentIndicators?.add(ComponentIndicator.fromJson(e));
              }
            });
          }
        }

        notifyListeners();
        return;
      } else {
        print(res.toString());
        sLoad();
        showError(() {}, msg: res.message!);
      }
    } catch (e, stack) {
      print(stack.toString());
      sLoad();
      debugPrint(e.toString());
      showError(() {}, msg: errorMessage);
    }
  }

  getPDOIndicator() async {
    dropKeyboard();
    iLoad();
    try {
      ResModel res = await getIt<ComponentsApiService>().getPDOIndicators();
      if (res.success!) {
        sLoad();

        // res.data.forEach((e) {
        //   if (e != null) {
        //     appCache.pdoIndicatorModel.add(PDOIndicatorModel.fromJson(e));
        //   }
        // });
        if (res.data != null) {
          // Check if data is a List
          if (res.data is List) {
            (res.data as List).forEach((e) {
              if (e != null) {
                appCache.pdoIndicatorModel.add(PDOIndicatorModel.fromJson(e));
              }
            });
          }
          // If data is a Map, extract the actual list (adjust key as needed)
          else if (res.data is Map && res.data.containsKey('results')) {
            (res.data['results'] as List).forEach((e) {
              if (e != null) {
                appCache.pdoIndicatorModel.add(PDOIndicatorModel.fromJson(e));
              }
            });
          }
        }

        notifyListeners();
        return;
      } else {
        print(res.toString());
        sLoad();
        showError(() {}, msg: res.message!);
      }
    } catch (e, stack) {
      print(stack.toString());
      sLoad();
      debugPrint(e.toString());
      showError(() {}, msg: errorMessage);
    }
  }

  void refresh() async {
    await getComponentsIndicator();
    await getPDOIndicator();
  }
}
