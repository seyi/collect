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
import '../../../widgets/input.dart';
import '../../../widgets/list-bottomsheet.dart';
import '../component.vm.dart';
import '../odk_collect_integration.dart';

class CatchmentInfoPage extends StatefulWidget {
  const CatchmentInfoPage({Key? key}) : super(key: key);

  @override
  _CatchmentInfoPageState createState() => _CatchmentInfoPageState();
}

class _CatchmentInfoPageState extends State<CatchmentInfoPage> {
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
                                  "Catchment",
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
                                    text: '1 /',
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

                        const Padding(
                          padding: EdgeInsets.symmetric(horizontal: 20.0),
                          child: const Text(
                            "Capture the Micro and Macro, Location of \ncatchments information",
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
                              text: "Name",
                            ),
                            DropdownInput<String>(
                              labelText: "Select Name",
                              title: "Tree Planting",
                              items: const ['Tree Planting', 'Watering'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),
                            const CustomText(
                              text: "State",
                            ),
                            DropdownInput<String>(
                              labelText: "Select State",
                              title: "State",
                              items: const ['Adamawa', 'Abuja'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),
                            const CustomText(
                              text: "Local Government Area (LGA)",
                            ),
                            DropdownInput<String>(
                              labelText: "Select LGA",
                              title: "LGA",
                              items: const ['Gwagwalada', 'AMAC'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),
                            const CustomText(
                              text: "Community",
                            ),
                            DropdownInput<String>(
                              labelText: "Select Community",
                              title: "Community",
                              items: const ['Community 1', 'Community 2'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),
                            const CustomText(
                              text: "Site",
                            ),
                            DropdownInput<String>(
                              labelText: "Site",
                              title: "Site",
                              items: const ['Site 1', 'Site 2'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),
                            const CustomText(
                              text: "GPS Coordinates",
                            ),
                            Row(
                              children: [
                                Expanded(
                                  flex: 6,
                                  child: Input(
                                    hintText: "0.008847748475",
                                    onChanged: (v) {},
                                    keyboardType: TextInputType.number,
                                  ),
                                ),
                                Expanded(
                                  child: GestureDetector(
                                    onTap: () {
                                      //Navigator.push(context, MaterialPageRoute(builder: (_) => WebViewSheet(onDone: () {}, url: misAppLink)));
                                      // Call the method to launch ODK Collect
                                      final odkIntegration = OdkCollectIntegration();
                                      odkIntegration.launchOdkCollect();

                                    },
                                    child: Column(
                                      children: [
                                        SvgPicture.asset('add-map'.svg),
                                        Text(
                                          "Add Map",
                                          style: TextStyle(fontSize: 8),
                                        )
                                      ],
                                    ),
                                  ),
                                ),
                              ],
                            ),
                            const CustomText(
                              text: "GPS Coordinates",
                            ),
                            Image.asset(
                              'imap'.png,
                              scale: 2.0,
                            ),
                            const CustomText(
                              text: "Description",
                            ),
                            Input(
                              hintText: "Description",
                              onChanged: (v) {},
                              keyboardType: TextInputType.text,
                            ),
                            16.sbH,
                            ACButton(
                              color: primaryColor,// model.selectedSubComponent == true ? primaryColor : Colors.grey,
                              onPressed: () => model.navigationService
                                  .navigateToReplace(Routes.activitiesInfoRoute), //{}, //=> model.hasEmail ? model.resetPassword() : model.showError(() {}, msg: "Please fill all Fields"),
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
