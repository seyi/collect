import 'package:flutter/foundation.dart';
import 'package:flutter_template/base.vm.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/model/component-model.dart';
import 'package:oktoast/oktoast.dart';

class ComponentViewModel extends BaseViewModel {
  bool isExpanded = false;
  // bool selectedSubComponent = false;

  ValueNotifier<SubComponentEnum?> selectedSubComponent = ValueNotifier(null);
  ValueNotifier<SemiSubComponentEnum?> selectedSemiSubComponent = ValueNotifier(null);
  List<String> subComponents = [
    "Sub Component Child 1",
    "Sub Component Child 2",
    "Sub Component Child 3",
  ];

  validate() {
    if (selectedSemiSubComponent.value != null) {
      if (selectedSubComponent.value?.semiSubComponents != null) {
        if (selectedSemiSubComponent.value != null) {
          //navigate with semi sub component
          navigationService.navigateTo(Routes.subComponentDashboardRoute,
              argument:
                  ComponentModel(componentEnum: selectedSubComponent.value!.getParentComponent, subComponentEnum: selectedSubComponent.value, semiSubComponentEnum: selectedSemiSubComponent.value));
        } else {
          showToast("Please select the necessary sub component");
        }
      } else {
        // navigate w/o semi sub components
        navigationService.navigateTo(Routes.subComponentDashboardRoute,
            argument: ComponentModel(componentEnum: selectedSubComponent.value!.getParentComponent, subComponentEnum: selectedSubComponent.value));
      }
    } else {
      showToast("Please select the necessary sub component");
    }
  }
}
