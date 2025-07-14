import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/svg.dart';
import 'package:flutter_template/constant/constants.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/widgets/custom-webview.dart';

import '../../../../base.ui.dart';
import '../../../../constant/palette.dart';
import '../../../../routes/routes.dart';
import '../../../widgets/custom-text.dart';
import '../../../widgets/custom_btn.dart';
import '../../../widgets/date-input.dart';
import '../../../widgets/dotted-button.dart';
import '../../../widgets/input.dart';
import '../../../widgets/list-bottomsheet.dart';
import '../../../widgets/result-framework.dart';
import '../component.vm.dart';

class SelectResultFrameworkPage extends StatefulWidget {
  const SelectResultFrameworkPage({Key? key}) : super(key: key);

  @override
  _SelectResultFrameworkPageState createState() => _SelectResultFrameworkPageState();
}

class _SelectResultFrameworkPageState extends State<SelectResultFrameworkPage> {
  @override
  Widget build(BuildContext context) {
    return BaseView<ComponentViewModel>(
        builder: (context, model, child) => Scaffold(
              appBar: AppBar(
                elevation: 0,
                backgroundColor: white,
                automaticallyImplyLeading: true,
                toolbarHeight: 10,
              ),
              backgroundColor: Colors.white,
              body: Column(
                children: [
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Row(
                              children: [
                                GestureDetector(
                                  onTap: () => model.navigationService.goBack(),
                                  child: Padding(
                                    padding: const EdgeInsets.symmetric(vertical: 5.0),
                                    child: SvgPicture.asset(
                                      'arrowback'.svg, // Ensure the correct path for your SVG
                                      width: 15,
                                    ),
                                  ),
                                ),
                                Padding(
                                  padding: EdgeInsets.symmetric(horizontal: 30.0),
                                  child: const Text(
                                    "Select Sub-Component \nResult Framework",
                                    style: TextStyle(
                                      color: Colors.black, // Replace with textDark
                                      fontSize: 11,
                                      fontWeight: FontWeight.w400,
                                    ),
                                    textAlign: TextAlign.left,
                                  ),
                                ),
                              ],
                            ),
                            const Text.rich(
                              TextSpan(
                                text: 'Step ',
                                style: TextStyle(
                                  color: Colors.black, // textDark color
                                  fontSize: 12,
                                ),
                                children: [
                                  TextSpan(
                                    text: '3 /',
                                    style: TextStyle(
                                      color: Colors.black, // textDark color
                                      fontWeight: FontWeight.bold,
                                    ),
                                  ),
                                  TextSpan(
                                    text: ' 4',
                                    style: TextStyle(
                                      color: Colors.green, // Green color
                                      fontWeight: FontWeight.bold,
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ],
                        ),


                      ],
                    ),
                  ),
                  SizedBox(height: 16),
                  Expanded(
                    child: SingleChildScrollView(
                      child: Container(
                        padding: EdgeInsets.symmetric(horizontal: 20.w),
                        child: Column(
                          children: [
                            20.sbH,
                            Align(
                              alignment: Alignment.centerLeft,
                              child: Text(
                                "Result Framework \n(PDO Level Indicators)",
                                style: TextStyle(
                                  color: Colors.black, // Replace with primaryDarkColor
                                  fontSize: 16,
                                  fontWeight: FontWeight.w600,
                                ),
                                textAlign: TextAlign.left,
                              ),
                            ),
                            10.sbH,
                            ResultFrameworkComponent(
                              title: "PDO Result Indicator Data",
                              iconPath: "ca".svg,
                              isSelected: false,
                              onSelected: (data) {
                                // Handle selection
                                print(data); // Output: {title: Profile, selected: true/false}
                              },
                            ),
                            30.sbH,
                            Align(
                              alignment: Alignment.centerLeft,
                              child: Text(
                                "Result Framework \n(Component Level Indicators)",
                                style: TextStyle(
                                  color: Colors.black, // Replace with primaryDarkColor
                                  fontSize: 16,
                                  fontWeight: FontWeight.w600,
                                ),
                                textAlign: TextAlign.left,
                              ),
                            ),


                            ResultFrameworkComponent(
                              title: "Component Result Indicator Data",
                              iconPath: "ca".svg,
                              isSelected: false,
                              onSelected: (data) {
                                // Handle selection
                                print(data); // Output: {title: Profile, selected: true/false}
                              },
                            ),
                            60.sbH,
                            ACButton(
                              color: model.selectedSubComponent == true ? primaryColor : Colors.grey,
                              onPressed: () => model.navigationService
                                  .navigateToReplace(Routes.beneficiaryInfoRoute), //{}, //=> model.hasEmail ? model.resetPassword() : model.showError(() {}, msg: "Please fill all Fields"),
                              text: "Save and Continue",
                              loadingState: model.viewState,
                            ),
                            16.sbH,
                          ],
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ));
  }

  Widget _buildIconWithText(String svgPath, String label) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        SvgPicture.asset(
          svgPath,
          width: 30,
          height: 30,
          placeholderBuilder: (BuildContext context) => Icon(Icons.error, size: 40, color: Colors.red),
        ),
        SizedBox(height: 8),
        Text(
          label,
          style: TextStyle(
            color: Colors.black,
            fontSize: 11,
            fontWeight: FontWeight.w500,
          ),
        ),
      ],
    );
  }
}
