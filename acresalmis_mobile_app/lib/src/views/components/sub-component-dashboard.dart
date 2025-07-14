import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/src/model/component-model.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/widgets/header.dart';

// import 'package:google_maps_flutter/google_maps_flutter.dart';

import '../../../base.ui.dart';
import '../../../routes/routes.dart';
import '../../widgets/custom_btn.dart';
import '../../widgets/listview-component.dart';
import 'component.vm.dart';

class SubComponentDashboardPage extends StatefulWidget {
  final ComponentModel componentModel;
  const SubComponentDashboardPage({super.key, required this.componentModel});

  @override
  _SubComponentDashboardPageState createState() => _SubComponentDashboardPageState();
}

class _SubComponentDashboardPageState extends State<SubComponentDashboardPage> {
  final List<Color> cardColors = [
    primaryColor,
    greenDark,
    secondaryColor,
    Colors.orange[800]!,
  ];

  // late GoogleMapController mapController;
  // // Initial camera position for the map
  // final CameraPosition _initialPosition = CameraPosition(
  //   target: LatLng(37.7749, -122.4194), // San Francisco (change coordinates to your desired location)
  //   zoom: 12,
  // );

  @override
  Widget build(BuildContext context) {
    return BaseView<ComponentViewModel>(
      builder: (context, model, child) => Scaffold(
        backgroundColor: Colors.white,
        body: Padding(
          padding: EdgeInsets.symmetric(horizontal: 20.w, vertical: 20.h),
          child: Column(
            children: [
              10.sbH,
              HeaderWidget(
                userName: model.userService.userCredentialsNotifier.value.name,
                userPicture: model.userService.userCredentialsNotifier.value.picture,
                greetingText: "Welcome Back",
                userNotifier: model.userService.userCredentialsNotifier,
              ),
              10.sbH,
              GestureDetector(
                onTap: () => model.navigationService.goBack(),
                child: Row(
                  children: [
                    Icon(
                      Icons.arrow_back,
                      color: greenAccent,
                      size: 18,
                    ),
                    10.sbW,
                    Text(
                      "Return Back",
                      style: TextStyle(
                        color: greenDark,
                        fontSize: 12.sp,
                        fontWeight: FontWeight.normal,
                      ),
                      textAlign: TextAlign.left,
                    ),
                  ],
                ),
              ),
              20.sbH,
              Expanded(
                child: SingleChildScrollView(
                  child: ConstrainedBox(
                    constraints: BoxConstraints(
                      minHeight: MediaQuery.of(context).size.height, // Ensures it fills the screen
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          "Component A - Dry-Land Management",
                          style: TextStyle(
                            color: Colors.black,
                            fontSize: 12.sp,
                            fontWeight: FontWeight.normal,
                          ),
                          textAlign: TextAlign.left,
                        ),
                        10.sbH,
                        Text(
                          "A1 - Summary of Strategic Watershed Management Planning Activities",
                          style: TextStyle(
                            color: Colors.black,
                            fontSize: 14.sp,
                            fontWeight: FontWeight.bold,
                          ),
                          textAlign: TextAlign.left,
                        ),
                        SizedBox(height: 20.h),

                        // Updated Card Scroll
                        SizedBox(
                          height: 185.h,
                          width: double.infinity,
                          child: PageView.builder(
                            scrollDirection: Axis.horizontal,
                            itemCount: cardColors.length,
                            itemBuilder: (context, index) {
                              return Padding(
                                padding: const EdgeInsets.only(right: 8.0), // Space between cards
                                child: ComponentsSummaryCard(
                                  title: "Multi-sectoral strategic watershed plans completed with appropriate analytical and stakeholder inputs",
                                  data: "₦3,000,000.00",
                                  buttonText: "Total Count",
                                  leftIcon: Icons.info_outline,
                                  rightIcon: Icons.arrow_forward_ios,
                                  backgroundColor: cardColors[index],
                                  overlayColor: Colors.transparent,
                                ),
                              );
                            },
                          ),
                        ),

                        SizedBox(height: 20.h),

                        Text(
                          "Interactive Map",
                          style: TextStyle(
                            color: Colors.black,
                            fontSize: 16.sp,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        SizedBox(height: 10.h),
                        Image.asset(
                          'imap'.png,
                          scale: 2.0,
                        ),

                        // Container(
                        //   height: 175.h, // Same height as the card section
                        //   width: double.infinity,
                        //   child: GoogleMap(
                        //     initialCameraPosition: _initialPosition,
                        //     onMapCreated: (GoogleMapController controller) {
                        //       mapController = controller;
                        //     },
                        //     myLocationEnabled: true, // Enable location on the map
                        //     compassEnabled: true, // Enable compass for orientation
                        //     zoomControlsEnabled: true, // Zoom controls
                        //   ),
                        // ),
                        20.sbH,
                        Row(
                          children: [
                            Text(
                              "Activities",
                              style: TextStyle(
                                color: Colors.black,
                                fontSize: 16.sp,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            Spacer(),
                            CustomButtonIcr(
                              text: "Create Activity",
                              icon: Icons.add,
                              onTap: () => model.navigationService.navigateToReplace(Routes.catchmentInfoRoute),
                              borderColor: primaryColor, // Custom border color
                              textColor: primaryColor, // Custom text color
                              iconColor: Colors.white, // Custom icon color
                              borderRadius: 12.0, // Custom border radius
                            )
                          ],
                        ),
                        ...List.generate(
                          2, // Replace with the desired number of items
                          (index) {
                            return Padding(
                              padding: const EdgeInsets.symmetric(vertical: 8.0),
                              child: CustomListCard(
                                title: "Preparation for Watershed Management Plan ${index + 1}",
                                subtitle: "Status Pending",
                                code: "A1-001",
                                imagePath: 'aci'.svg, // Replace with the correct path to your image
                                onEditPressed: () {
                                  // Handle edit action here
                                  print('Edit button pressed for item ${index + 1}');
                                },
                              ),
                            );
                          },
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class ComponentsSummaryCard extends StatelessWidget {
  final String title;
  final String data;
  final String buttonText;
  final IconData leftIcon;
  final IconData rightIcon;
  final Color backgroundColor;
  final Color overlayColor;

  const ComponentsSummaryCard({
    Key? key,
    required this.title,
    required this.data,
    required this.buttonText,
    required this.leftIcon,
    required this.rightIcon,
    required this.backgroundColor,
    required this.overlayColor,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 8),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(12),
        image: DecorationImage(
          image: AssetImage('pattern'.png), // Use a valid image asset path
          fit: BoxFit.cover,
          colorFilter: ColorFilter.mode(
            overlayColor.withOpacity(0.9),
            BlendMode.darken,
          ),
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: TextStyle(
              color: Colors.white,
              fontSize: 12,
              fontWeight: FontWeight.normal,
            ),
          ),
          const SizedBox(height: 16),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                data,
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 20,
                  fontFamily: "nunito",
                  fontWeight: FontWeight.w700,
                ),
              ),
              Icon(
                rightIcon,
                color: Colors.white,
                size: 24,
              ),
            ],
          ),
          const Spacer(),
          Row(
            children: [
              ElevatedButton.icon(
                onPressed: () {},
                label: Text(
                  buttonText,
                  style: TextStyle(color: primaryColor, fontSize: 11.sp),
                ),
                style: ElevatedButton.styleFrom(
                  elevation: 0,
                  backgroundColor: Colors.white,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(30),
                  ),
                ),
              ),
              Spacer(),
              SmallAvatar(
                imagePath: 'ai'.png, // Replace with your asset path
                isAsset: true,
                size: 20.0,
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class SmallAvatar extends StatelessWidget {
  final String imagePath; // Path to the image (asset or network)
  final bool isAsset; // True if the image is from assets, false for network
  final double size; // Size of the avatar

  const SmallAvatar({
    Key? key,
    required this.imagePath,
    this.isAsset = true, // Default to asset images
    this.size = 50.0, // Default size is 50
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        color: Colors.white, // White background
        shape: BoxShape.circle, // Circular container
        boxShadow: [
          BoxShadow(
            color: Colors.grey.withOpacity(0.3),
            blurRadius: 4,
            offset: const Offset(0, 2), // Adds subtle shadow
          ),
        ],
      ),
      child: Center(
        child: ClipOval(
          child: isAsset
              ? Image.asset(
                  imagePath,
                  width: size * 0.6, // Adjust icon size
                  height: size * 0.6,
                  fit: BoxFit.cover,
                  errorBuilder: (context, error, stackTrace) => Icon(
                    Icons.broken_image,
                    size: size * 0.4,
                    color: Colors.grey,
                  ),
                )
              : Image.network(
                  imagePath,
                  width: size * 0.6, // Adjust icon size
                  height: size * 0.6,
                  fit: BoxFit.cover,
                  errorBuilder: (context, error, stackTrace) => Icon(
                    Icons.broken_image,
                    size: size * 0.4,
                    color: Colors.grey,
                  ),
                ),
        ),
      ),
    );
  }
}
