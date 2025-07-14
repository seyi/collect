import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_template/base.ui.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/routes/routes.dart';
import 'package:flutter_template/src/model/component-model.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/views/home/home.vm.dart';
import 'package:flutter_template/src/widgets/carousel.dart';
import 'package:flutter_template/src/widgets/header.dart';
import 'package:flutter_template/src/widgets/loading-wrapper.dart';
import 'package:pie_chart/pie_chart.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  Widget build(BuildContext context) {
    return BaseView<HomeViewModel>(
      builder: (context, model, child) => RefreshIndicator(
        onRefresh: () async {
          model.refresh();
        },
        child: LoadingWrapper(
          isLoading: model.loader,
          child: Scaffold(
            backgroundColor: AppColors.white,
            body: Column(
              children: [
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 18.0, vertical: 8),
                  child: HeaderWidget(
                    userName: model.userService.userCredentialsNotifier.value.name,
                    userPicture: model.userService.userCredentialsNotifier.value.picture,
                    greetingText: "Welcome Back",
                    userNotifier: model.userService.userCredentialsNotifier,
                  ),
                ),
                Expanded(
                  child: SingleChildScrollView(
                    child: Column(
                      children: [
                        Container(
                          margin: const EdgeInsets.all(16),
                          padding: const EdgeInsets.all(18),
                          decoration: BoxDecoration(
                            color: const Color(0xFFEAF6E8), // Primary light color from your image
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              // Weather Icon on the left

                              SvgPicture.asset(
                                'weather'.svg,
                                height: 50,
                                width: 50,
                              ),
                              30.sbW,
                              // Middle column for location and temperature
                              Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  const Text(
                                    "Location",
                                    style: TextStyle(
                                      color: Colors.black54,
                                      fontSize: 14,
                                      fontWeight: FontWeight.w500,
                                    ),
                                  ),
                                  4.sbH,
                                  const Text(
                                    "Abuja",
                                    style: TextStyle(
                                      color: Colors.black87,
                                      fontSize: 16,
                                      fontWeight: FontWeight.w600,
                                    ),
                                  ),
                                  const SizedBox(height: 4),
                                  const Text(
                                    "25 °C | °F",
                                    style: TextStyle(
                                      color: Colors.black54,
                                      fontSize: 14,
                                      fontWeight: FontWeight.w400,
                                    ),
                                  ),
                                ],
                              ),
                              const Spacer(),

                              // Right column for weather details
                              Column(
                                crossAxisAlignment: CrossAxisAlignment.end,
                                children: [
                                  const Text(
                                    "Precipitation: 1%",
                                    style: TextStyle(
                                      color: Colors.black54,
                                      fontSize: 14,
                                      fontWeight: FontWeight.w400,
                                    ),
                                  ),
                                  4.sbH,
                                  const Text(
                                    "Humidity: 80%",
                                    style: TextStyle(
                                      color: Colors.black54,
                                      fontSize: 14,
                                      fontWeight: FontWeight.w400,
                                    ),
                                  ),
                                  const SizedBox(height: 4),
                                  const Text(
                                    "Wind: 8 km/h",
                                    style: TextStyle(
                                      color: Colors.black54,
                                      fontSize: 14,
                                      fontWeight: FontWeight.w400,
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                        Padding(
                          padding: EdgeInsets.symmetric(horizontal: 18.w),
                          child: const Align(
                            alignment: Alignment.centerLeft,
                            child: Text(
                              "Components",
                              style: TextStyle(
                                color: Colors.black87,
                                fontSize: 18,
                                fontWeight: FontWeight.w900,
                              ),
                            ),
                          ),
                        ),
                        // GestureDetector(
                        //     onTap: () {
                        //       model.navigationService.navigateTo(Routes.polyPage);
                        //     },
                        //     child: const Icon(Icons.add)),
                        const ServiceGrid(),
                        SizedBox(height: 25.h),
                        Padding(
                          padding: EdgeInsets.symmetric(horizontal: 18.w),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              const Text(
                                "Summary",
                                style: TextStyle(
                                  color: Colors.black87,
                                  fontSize: 15,
                                  fontWeight: FontWeight.w900,
                                ),
                              ),

                              RichText(
                                text: TextSpan(
                                  children: [
                                    TextSpan(
                                      text: "Since 1 year ago\n",
                                      style: TextStyle(
                                        color: greenDark,
                                        fontSize: 14,
                                        fontWeight: FontWeight.w900,
                                      ),
                                    ),
                                    const TextSpan(
                                      text: "Wed, November 2024",
                                      style: TextStyle(
                                        color: Colors.grey,
                                        fontSize: 12,
                                        fontWeight: FontWeight.normal,
                                      ),
                                    ),
                                  ],
                                ),
                              ),

                              // Wed, November 2024
                            ],
                          ),
                        ),
                        20.sbH,
                        model.dataMap.isEmpty || model.dataMap.values.every((value) => value == 0)
                            ? const Text(
                                "No data available",
                                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                              )
                            : Padding(
                                padding: const EdgeInsets.symmetric(vertical: 16.0),
                                child: PieChart(
                                  dataMap: model.dataMap,
                                  animationDuration: const Duration(milliseconds: 800),
                                  chartLegendSpacing: 32,
                                  chartRadius: MediaQuery.of(context).size.width / 1.8,
                                  colorList: model.colorList,
                                  initialAngleInDegree: 0,
                                  chartType: ChartType.ring,
                                  ringStrokeWidth: 32,
                                  //centerText: "120",
                                  centerWidget: Column(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    crossAxisAlignment: CrossAxisAlignment.center,
                                    children: [
                                      const Text("Total No. of Activities"),
                                      5.sbH,
                                      const Text(
                                        "120",
                                        style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                                      )
                                    ],
                                  ),
                                  legendOptions: const LegendOptions(
                                    showLegendsInRow: true,
                                    legendPosition: LegendPosition.bottom,
                                    showLegends: true,
                                    legendShape: BoxShape.circle,
                                    legendTextStyle: TextStyle(
                                      fontWeight: FontWeight.bold,
                                    ),
                                  ),
                                  chartValuesOptions: const ChartValuesOptions(
                                    showChartValueBackground: true,
                                    showChartValues: true,
                                    showChartValuesInPercentage: true,
                                    showChartValuesOutside: true,
                                    decimalPlaces: 1,
                                  ),
                                ),
                              ),
                        ImageCarousel2(imageUrls: const []),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class ServiceGrid extends StatefulWidget {
  const ServiceGrid({Key? key}) : super(key: key);

  @override
  State<ServiceGrid> createState() => _ServiceGridState();
}

class _ServiceGridState extends State<ServiceGrid> {
  int? selectedIndex;

  final List<ComponentType> services = [ComponentType.componentA, ComponentType.componentB, ComponentType.componentC, ComponentType.componentD];

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 18),
      child: GridView.builder(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          mainAxisSpacing: 16,
          crossAxisSpacing: 16,
          childAspectRatio: 1,
        ),
        itemCount: services.length,
        itemBuilder: (context, index) {
          final isLarge = index % 2 == 0;
          final height = isLarge ? 150.0 : 100.0;
          final width = isLarge ? 140.0 : 99.0;

          // setState(() => selectedIndex = index);
          //               // Navigator.pushNamed(context, services[index].route!);
          return ServiceCard(
            componentEnum: ComponentEnum.values[index],
            service: services[index],
            isSelected: selectedIndex == index,
            height: height,
            width: width,
          );
        },
      ),
    );
  }
}

class ServiceCard extends StatelessWidget {
  final ComponentType service;
  final ComponentEnum componentEnum;
  final bool isSelected;
  final double height;
  final double width;

  const ServiceCard({Key? key, required this.service, required this.isSelected, required this.height, required this.width, required this.componentEnum}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => componentEnum.subComponentEnum?.length != 0
          ? Navigator.pushNamed(context, Routes.selectSubComponentRoute, arguments: componentEnum)
          : Navigator.pushNamed(context, Routes.subComponentDashboardRoute, arguments: ComponentModel(componentEnum: componentEnum)),
      child: Container(
        height: isSelected ? height * 1.2 : height, // Bigger height for selected card
        width: isSelected ? width * 1.2 : width, // Adjust width proportionally
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: cardBg,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: isSelected ? const Color(0xff2B7C44) : const Color(0xffE0F6FF),
            width: isSelected ? 2 : 1, // Thicker border for selected card
          ),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 8,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Top icons
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                SvgPicture.asset(
                  'refresh'.svg,
                  height: 20,
                  width: 20,
                ),
                const SizedBox(width: 8),
                SvgPicture.asset(
                  'details'.svg,
                  height: 20,
                  width: 20,
                ),
              ],
            ),
            const SizedBox(height: 8),

            // Component icon
            SvgPicture.asset(
              service.icon ?? '',
              height: 32,
              width: 32,
              color: isSelected ? const Color(0xff2B7C44) : Colors.grey,
            ),
            const SizedBox(height: 16),

            // Title
            Text(
              componentEnum.name ?? "",
              // service.title ?? "",
              style: TextStyle(
                fontSize: isSelected ? 16 : 14,
                fontWeight: FontWeight.bold,
                color: isSelected ? const Color(0xff2B7C44) : Colors.black,
              ),
            ),
            const SizedBox(height: 8),

            // Description
            Text(
              componentEnum.label ?? "",
              // service.description ?? "",
              style: const TextStyle(
                fontSize: 10,
                fontWeight: FontWeight.w400,
                color: Colors.black,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class ComponentType {
  final String? title;
  final String? icon;
  final String? route;
  final String? description;

  ComponentType({this.title, this.icon, this.route, this.description});

  static final componentA = ComponentType(
    title: 'Component A',
    description: 'Dry-land Management',
    icon: 'assets/images/ca.svg',
    route: Routes.selectSubComponentRoute,
  );

  static final componentB = ComponentType(
    title: 'Component B',
    description: 'Community Climate Resilience',
    icon: 'assets/images/cb.svg',
    route: Routes.selectSubComponentRoute,
  );

  static final componentC = ComponentType(
    title: 'Component C',
    description: 'Institutional Strengthening and Project Management ',
    icon: 'assets/images/cc.svg',
    route: Routes.selectSubComponentRoute,
  );
  static final componentD = ComponentType(
    title: 'Component D',
    description: 'Contingent Emergency Response',
    icon: 'assets/images/cd.svg',
    route: Routes.selectSubComponentRoute,
  );
}
