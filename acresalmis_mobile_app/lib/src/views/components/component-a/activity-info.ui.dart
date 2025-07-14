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
import '../component.vm.dart';

class ActivitiesInfoPage extends StatefulWidget {
  const ActivitiesInfoPage({Key? key}) : super(key: key);

  @override
  _ActivitiesInfoPageState createState() => _ActivitiesInfoPageState();
}

class _ActivitiesInfoPageState extends State<ActivitiesInfoPage> {
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
                                SizedBox(width: 15),
                                Text(
                                  "Activity Information",
                                  style: TextStyle(
                                    color: Colors.black, // Replace with primaryDarkColor
                                    fontSize: 16,
                                    fontWeight: FontWeight.w600,
                                  ),
                                  textAlign: TextAlign.left,
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
                                    text: '2 /',
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
                        // const SizedBox(height: 10),
                        const Padding(
                          padding: EdgeInsets.symmetric(horizontal: 30.0),
                          child: const Text(
                            "Set an activity target for to drive impact!",
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
                  ),
                  SizedBox(height: 16),
                  Expanded(
                    child: SingleChildScrollView(
                      child: Container(
                        padding: EdgeInsets.symmetric(horizontal: 20.w),
                        child: Column(
                          children: [
                            20.sbH,
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceAround,
                              children: [
                                _buildIconWithText('benefactors'.svg, 'Beneficiaries'),
                                _buildIconWithText('milestones'.svg, 'Project Milestone'),
                                _buildIconWithText('docs'.svg, 'Documents'),
                                _buildIconWithText('media'.svg, 'Media'),
                              ],
                            ),
                            10.sbH,
                            const CustomText(
                              text: "Project Id",
                            ),
                            Input(
                              hintText: "P175237",
                              onChanged: (v) {},
                              enabled: false,
                              keyboardType: TextInputType.text,
                            ),

                            const CustomText(
                              text: "Credit Id",
                            ),
                            Input(
                              hintText: "IDA-70150",
                              onChanged: (v) {},
                              enabled: false,
                              keyboardType: TextInputType.text,
                            ),
                            const CustomText(
                              text: "Select Component",
                            ),


                            DropdownInput<String>(
                              labelText: "Select Component",
                              title: "Select Component",
                              items: const ['Component A', 'Component B'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),
                            const CustomText(
                              text: "Select Sub Component",
                            ),
                            DropdownInput<String>(
                              labelText: "Select Sub Component",
                              title: "Subcomponent",
                              items: const ['Item 1', 'Item 2'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),
                            const CustomText(
                              text: "Code",
                            ),
                            Input(
                              hintText: "A1-003",
                              onChanged: (v) {},
                              enabled: false,
                              keyboardType: TextInputType.text,
                            ),
                            const CustomText(
                              text: "Activity Type",
                            ),
                            DropdownInput<String>(
                              labelText: "Activity Type",
                              title: "Activity Type",
                              items: const ['Type 1', 'Type 2'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),
                            const CustomText(
                              text: "Description",
                            ),
                            Input(
                              hintText: "Tree Planting Description",
                              onChanged: (v) {},
                              keyboardType: TextInputType.text,
                            ),

                            const CustomText(
                              text: " Expected Start Date",
                            ),

                            DateInput(
                              labelText: "Select Date",
                              hintText: "Enter Your Date",
                              controller: TextEditingController(),
                            ),


                            const CustomText(
                              text: "Actual Start Date",
                            ),
                            DateInput(
                              labelText: "Actual Start Date",
                              hintText: "Enter Actual Start Date",
                              controller: TextEditingController(),
                            ),

                            const CustomText(
                              text: "Actual End Date",
                            ),
                            DateInput(
                              labelText: "Actual End Date",
                              hintText: "Enter Actual End Date",
                              controller: TextEditingController(),
                            ),

                            const CustomText(
                              text: "Budgeted Amount ",
                            ),
                            Input(
                              hintText: "50000",
                              onChanged: (v) {},
                              keyboardType: TextInputType.number,
                            ),
                            const CustomText(
                              text: " Amount Disbursed USD",
                            ),
                            Input(
                              hintText: " 2,976.63",
                              onChanged: (v) {},
                              keyboardType: TextInputType.number,
                            ),
                            10.sbH,
                            DottedUploadContainer(
                              onTap: () {
                                // Handle upload action here
                                print("Container tapped");
                              },
                            ),
                            10.sbH,
                            const CustomText(
                              text: "Status",
                            ),
                            DropdownInput<String>(
                              labelText: "Status",
                              title: "Status",
                              items: const ['Not Commenced', 'Commenced'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),

                            16.sbH,
                            ACButton(
                              color: model.selectedSubComponent == true ? primaryColor : Colors.grey,
                              onPressed: () => model.navigationService
                                  .navigateToReplace(Routes.activitySubComponentInfoRoute), //{}, //=> model.hasEmail ? model.resetPassword() : model.showError(() {}, msg: "Please fill all Fields"),
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
