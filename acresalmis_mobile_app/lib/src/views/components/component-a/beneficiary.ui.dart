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
import '../../../widgets/profile-avatar.dart';
import '../component.vm.dart';

class BeneficiaryInfoPage extends StatefulWidget {
  const BeneficiaryInfoPage({Key? key}) : super(key: key);

  @override
  _BeneficiaryInfoPageState createState() => _BeneficiaryInfoPageState();
}

class _BeneficiaryInfoPageState extends State<BeneficiaryInfoPage> {
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
                                  "Capture Beneficiary",
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
                                    text: '4/',
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
                          padding: EdgeInsets.symmetric(horizontal: 30.0),
                          child: const Text(
                            "Capture a new beneficiary information",
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

                          Align(
                            alignment: Alignment.centerRight,
                            child: ProfileImageUploader(
                              imageUrl: "https://via.placeholder.com/150", // Replace with your image URL
                              onCameraTap: () {
                                // Handle camera icon tap
                                print("Camera tapped");
                              },
                              onUploadTap: () {
                                // Handle upload tap
                                print("Upload clicked");
                              },
                            ),
                          ),

                            Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Expanded(
                                  child: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      const CustomText(
                                        text: "National Id",
                                      ),
                                      Input(
                                        hintText: "221300983",
                                        onChanged: (v) {},
                                        enabled: false,
                                        keyboardType: TextInputType.text,
                                      ),
                                    ],
                                  ),
                                ),
                                const SizedBox(width: 16), // Add spacing between the columns
                                Expanded(
                                  child: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      const CustomText(
                                        text: "Title",
                                      ),
                                      DropdownInput<String>(
                                        labelText: "Select Title",
                                        title: "Select Title",
                                        items: const ['Mr.', 'Mrs.', 'Chief'],
                                        itemBuilder: (v) => ListTile(title: Text(v)),
                                        onSelected: (v) => print(
                                          v.toString(),
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ],
                            ),


                            const CustomText(
                              text: "First Name",
                            ),
                            Input(
                              hintText: "Enter FirstName",
                              onChanged: (v) {},
                              keyboardType: TextInputType.text,
                            ),

                            const CustomText(
                              text: "Middle Name",
                            ),
                            Input(
                              hintText: "Enter MiddleName",
                              onChanged: (v) {},
                              keyboardType: TextInputType.text,
                            ),

                            const CustomText(
                              text: "Last Name",
                            ),
                            Input(
                              hintText: "Enter LastName",
                              onChanged: (v) {},
                              keyboardType: TextInputType.text,
                            ),
                            const CustomText(
                              text: "Select Gender",
                            ),
                            DropdownInput<String>(
                              labelText: "Select Gender",
                              title: "Select Gender",
                              items: const ['Male', 'Female'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),
                            const CustomText(
                              text: "Select Marital Status",
                            ),
                            DropdownInput<String>(
                              labelText: "Select Marital Status",
                              title: "Marital Status",
                              items: const ['Single', 'Married', 'Divoiced', 'Seperated'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),
                            const CustomText(
                              text: "Community Interest Group",
                            ),
                            DropdownInput<String>(
                              labelText: "Select Community Interest Group",
                              title: "Community Interest Group",
                              items: const ['Group 1', 'Group 2', 'Group 3', 'Group 4'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),

                            const CustomText(
                              text: "Age Bracket",
                            ),
                            DropdownInput<String>(
                              labelText: "Select Age Bracket",
                              title: "Age Bracket",
                              items: const ['15-25', '35-45'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),
                            const CustomText(
                              text: "Primary Occupation",
                            ),
                            DropdownInput<String>(
                              labelText: "Select Primary Occupation",
                              title: "Primary Occupation",
                              items: const ['Farmer', 'ICT'],
                              itemBuilder: (v) => ListTile(title: Text(v)),
                              onSelected: (v) => print(
                                v.toString(),
                              ),
                            ),
                            const CustomText(
                              text: "Household Size",
                            ),
                            Input(
                              hintText: "Enter Household Size",
                              onChanged: (v) {},
                              keyboardType: TextInputType.text,
                            ),

                            const CustomText(
                              text: "Date Registered",
                            ),
                            DateInput(
                              labelText: "Select Date Registered",
                              hintText: "Enter Your Date Registered",
                              controller: TextEditingController(),
                            ),

                            const CustomText(
                              text: "Primary Phone Number",
                            ),
                            Input(
                              hintText: "Enter Primary Phone Number",
                              onChanged: (v) {},
                              keyboardType: TextInputType.text,
                            ),

                            const CustomText(
                              text: "Secondary Phone No.",
                            ),
                            Input(
                              hintText: "Enter Secondary Phone Number",
                              onChanged: (v) {},
                              keyboardType: TextInputType.text,
                            ),

                            const CustomText(
                              text: "Email",
                            ),
                            Input(
                              hintText: "Enter Email",
                              onChanged: (v) {},
                              keyboardType: TextInputType.text,
                            ),

                            16.sbH,
                            ACButton(
                              color: primaryColor,  //model.selectedSubComponent == true ? primaryColor : Colors.grey,
                              onPressed: () => model.navigationService
                                  .navigateToReplace(Routes.homeRoute), //{}, //=> model.hasEmail ? model.resetPassword() : model.showError(() {}, msg: "Please fill all Fields"),
                              text: "Submit",
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
