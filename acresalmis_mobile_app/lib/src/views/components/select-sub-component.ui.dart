import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/svg.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/src/model/component-model.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/views/components/component.vm.dart';
import 'package:flutter_template/src/widgets/custom_btn.dart';

import '../../widgets/sub-component.dart';

class SelectSubComponentPage extends StatefulWidget {
  final ComponentEnum component;
  const SelectSubComponentPage({Key? key, required this.component}) : super(key: key);

  @override
  _SelectSubComponentPageState createState() => _SelectSubComponentPageState();
}

class _SelectSubComponentPageState extends State<SelectSubComponentPage> {
  @override
  Widget build(BuildContext context) {
    return BaseView<ComponentViewModel>(
      builder: (context, model, child) => Scaffold(
        backgroundColor: white,
        body: Container(
          padding: EdgeInsets.symmetric(horizontal: 20.w),
          child: Column(
            children: [
              60.sbH,
              Align(
                alignment: Alignment.centerLeft,
                child: GestureDetector(
                  onTap: () => model.navigationService.goBack(),
                  child: Padding(
                    padding: const EdgeInsets.symmetric(vertical: 5.0),
                    child: SvgPicture.asset(
                      'arrowback'.svg, // Ensure the correct path for your SVG
                      width: 15,
                    ),
                  ),
                ),
              ),
              10.sbH,
              Align(
                alignment: Alignment.centerLeft,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      "${widget.component.name}: ${widget.component.label}",
                      style: TextStyle(color: primaryDarkColor, fontSize: 16.w, fontWeight: FontWeight.w600),
                      textAlign: TextAlign.left,
                    ),
                    5.sbH,
                    Text(
                      "Sub-Component",
                      style: TextStyle(color: textLight, fontSize: 12.sp, fontWeight: FontWeight.w400),
                      textAlign: TextAlign.left,
                    ),
                  ],
                ),
              ),
              34.sbH,
              ValueListenableBuilder(
                  valueListenable: model.selectedSubComponent,
                  builder: (context, sc, _) => ValueListenableBuilder(
                        valueListenable: model.selectedSemiSubComponent,
                        builder: (context, semiSC, _) => Column(
                          children: [
                            ...widget.component.subComponentEnum!.map(
                              (subComponents) => ISubComponent(
                                subComponentEnum: subComponents,
                                title: "${subComponents.name}: ${widget.component.label}",
                                iconPath: subComponents.getParentComponent.icon!,
                                hasChildren: subComponents.semiSubComponents != null, // Toggle to false for no children
                                children: model.subComponents,
                                isSelectedSubcomponent: subComponents == sc,
                                selectedSemiSubcomponent: semiSC,
                                onSelected: (subComp, semiSubComp) {
                                  model.selectedSubComponent.value = subComp;
                                  if (semiSubComp != null) {
                                    model.selectedSemiSubComponent.value = semiSubComp;
                                  }
                                },
                              ),
                            ),
                          ],
                        ),
                      )),
              Expanded(
                child: Container(),
              ),
              16.sbH,
              ACButton(
                color: (model.selectedSubComponent.value != null || model.selectedSemiSubComponent.value != null) ? primaryColor : Colors.grey,
                onPressed: () => model.validate(), //{}, //=> model.hasEmail ? model.resetPassword() : model.showError(() {}, msg: "Please fill all Fields"),
                text: "Continue",
                loadingState: model.viewState,
              ),
              16.sbH,
            ],
          ),
        ),
      ),
    );
  }
}
//D4EDD6
