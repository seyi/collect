// import 'package:flutter/material.dart';
// import 'package:flutter_screenutil/flutter_screenutil.dart';
// import 'package:flutter_svg/flutter_svg.dart';
// import 'package:nourisha/constant/palette.dart';
// import 'package:nourisha/core/services/analytics-services.dart';
// import 'package:nourisha/core/services/app-cache.dart';
// import 'package:nourisha/core/services/user.service.dart';
// import 'package:nourisha/core/styles/text-styles.dart';
// import 'package:nourisha/locator.dart';
// import 'package:nourisha/routes/routes.dart';
// import 'package:nourisha/utils/string%20utils.dart';
// import 'package:nourisha/utils/string-extensions.dart';
// import 'package:nourisha/utils/widget_extensions.dart';
// import 'package:shimmer/shimmer.dart';
//
// class GiftCardWidget extends StatelessWidget {
//   const GiftCardWidget({
//     super.key,
//   });
//
//   @override
//   Widget build(BuildContext context) {
//     return GestureDetector(
//       behavior: HitTestBehavior.opaque,
//       onTap: () => Navigator.pushNamed(context, Routes.giftcards),
//       child: Container(
//         padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 16),
//         decoration: BoxDecoration(
//             color: AppColors.lightGreen,
//             borderRadius: BorderRadius.circular(12)),
//         child: Column(
//           crossAxisAlignment: CrossAxisAlignment.start,
//           children: [
//             Text(
//               "Nourisha Gift Cards",
//               style: AppStyles.nStyle.copyWith(
//                   color: AppColors.darkGreen,
//                   fontSize: 24.sp,
//                   fontWeight: FontWeight.bold),
//               overflow: TextOverflow.ellipsis,
//             ),
//             4.sbH,
//             Text(
//               "Give the gift of chef made meals to your loved ones",
//               style: AppStyles.nStyle.copyWith(
//                   color: AppColors.textRider,
//                   fontSize: 12.sp,
//                   fontWeight: FontWeight.w400),
//             ),
//             8.sbH,
//             Container(
//                 padding:
//                 const EdgeInsets.symmetric(vertical: 8, horizontal: 12),
//                 decoration: BoxDecoration(
//                     color: AppColors.darkGreen.withOpacity(0.1),
//                     borderRadius: BorderRadius.circular(18)),
//                 child: Text(
//                   "Get Started",
//                   style: AppStyles.nStyle.copyWith(
//                       color: AppColors.darkGreen,
//                       fontSize: 14.sp,
//                       fontWeight: FontWeight.w600),
//                 ))
//           ],
//         ),
//       ),
//     );
//   }
// }
//
// class ReferFriend2 extends StatelessWidget {
//   const ReferFriend2({
//     super.key,
//   });
//
//   @override
//   Widget build(BuildContext context) {
//     return Stack(
//       children: [
//         Container(
//           padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 16),
//           decoration: BoxDecoration(
//               color: primaryColor, borderRadius: BorderRadius.circular(12)),
//           child: Column(
//             crossAxisAlignment: CrossAxisAlignment.start,
//             children: [
//               Text(
//                 "Get £10 for every Referral",
//                 style: AppStyles.nStyle.copyWith(
//                     color: white, fontSize: 24.sp, fontWeight: FontWeight.bold),
//                 overflow: TextOverflow.ellipsis,
//               ),
//               4.sbH,
//               Text(
//                 "Earn £10 for every friend, or anyone who subscribes with your referral code! No limits on referrals. Let’s goooo!",
//                 style: AppStyles.nStyle.copyWith(
//                     color: white, fontSize: 12.sp, fontWeight: FontWeight.w400),
//               ),
//               8.sbH,
//               GestureDetector(
//                 behavior: HitTestBehavior.opaque,
//                 onTap: () =>
//                     Navigator.pushNamed(context, Routes.referAFriendRoute),
//                 child: Container(
//                     padding: const EdgeInsets.symmetric(
//                         vertical: 10, horizontal: 25),
//                     decoration: BoxDecoration(
//                         color: white, borderRadius: BorderRadius.circular(10)),
//                     child: Text(
//                       "Refer a friend",
//                       style: AppStyles.nStyle.copyWith(
//                           color: primaryColor,
//                           fontSize: 14.sp,
//                           fontWeight: FontWeight.w600),
//                     )),
//               )
//             ],
//           ),
//         ),
//         Positioned(
//           right: 0,
//           bottom: -10,
//           child: Image.asset('ent'.png),
//         ),
//       ],
//     );
//   }
// }
//
// class HelpWidget2 extends StatelessWidget {
//   final Function() onTap;
//   const HelpWidget2({super.key, required this.onTap});
//
//   @override
//   Widget build(BuildContext context) {
//     return GestureDetector(
//       behavior: HitTestBehavior.opaque,
//       onTap: onTap,
//       child: Container(
//         padding: EdgeInsets.symmetric(horizontal: 20.w, vertical: 12.h),
//         decoration: const BoxDecoration(
//           color: Color(0xff4AC375),
//         ),
//         child: Row(
//           mainAxisAlignment: MainAxisAlignment.spaceBetween,
//           children: [
//             Text(
//               "Need Help?",
//               style: AppStyles.nStyle.copyWith(
//                   color: white, fontSize: 14.sp, fontWeight: FontWeight.w600),
//             ),
//             Row(
//               mainAxisSize: MainAxisSize.min,
//               children: [
//                 Text(
//                   "Send Message",
//                   style: AppStyles.nStyle.copyWith(
//                       color: white,
//                       fontSize: 14.sp,
//                       decoration: TextDecoration.underline,
//                       decorationColor: white,
//                       fontWeight: FontWeight.w600),
//                 ),
//                 4.sbW,
//                 SvgPicture.asset('send-message'.svg)
//               ],
//             )
//           ],
//         ),
//       ),
//     );
//   }
// }
//
// class ReferFriendWidget extends StatelessWidget {
//   final String? title;
//   final String? subTitle;
//   const ReferFriendWidget({
//     Key? key,
//     this.title,
//     this.subTitle,
//   }) : super(key: key);
//
//   @override
//   Widget build(BuildContext context) {
//     return Stack(
//       children: [
//         Container(
//           decoration: BoxDecoration(
//               color: const Color(0xffFFEFC6),
//               borderRadius: BorderRadius.circular(12)),
//           child: Row(
//             crossAxisAlignment: CrossAxisAlignment.start,
//             children: [
//               Expanded(
//                 flex: 1,
//                 child: Container(),
//               ),
//               16.sbW,
//               Expanded(
//                 flex: 3,
//                 child: Column(
//                   crossAxisAlignment: CrossAxisAlignment.start,
//                   children: [
//                     16.sbH,
//                     Padding(
//                       padding: const EdgeInsets.symmetric(horizontal: 8),
//                       child: Text(
//                         title ?? "Get £10 for Every Referral!",
//                         style: AppStyles.nStyle.copyWith(
//                             fontWeight: FontWeight.w700,
//                             fontSize: title == null ? 18.sp : 16.sp),
//                       ),
//                     ),
//                     2.sbH,
//                     Padding(
//                       padding: const EdgeInsets.symmetric(horizontal: 8),
//                       child: Text(
//                         subTitle ??
//                             "Earn £10 for every friend, or anyone, who subscribes with your referral code! No limits on referrals. Let's gooooo!",
//                         style: AppStyles.nStyle.copyWith(
//                             color: const Color(0xff565C69),
//                             fontWeight: FontWeight.w400,
//                             fontSize: 12.sp),
//                       ),
//                     ),
//                     20.sbH,
//                     GestureDetector(
//                       onTap: () => Navigator.pushNamed(
//                           context, Routes.referAFriendRoute),
//                       child: Padding(
//                         padding: EdgeInsets.symmetric(horizontal: 16.w),
//                         child: Align(
//                           alignment: Alignment.centerRight,
//                           child: Text(
//                             'Refer Friend',
//                             style: AppStyles.nStyle.copyWith(
//                                 color: const Color(0xFFFE0000),
//                                 fontWeight: FontWeight.w600,
//                                 decoration: TextDecoration.underline,
//                                 fontSize: 14.sp),
//                           ),
//                         ),
//                       ),
//                     ),
//                     16.sbH,
//                   ],
//                 ),
//               )
//             ],
//           ),
//         ),
//         Positioned(
//           left: -10,
//           bottom: 0,
//           child: Image.asset(
//             'announce'.png,
//             height: 100,
//             width: 120.w,
//           ),
//         ),
//       ],
//     );
//   }
// }
//
// class UpdateMealPlanWidget extends StatelessWidget {
//   final Function function;
//   const UpdateMealPlanWidget({
//     Key? key,
//     required this.function,
//   }) : super(key: key);
//
//   @override
//   Widget build(BuildContext context) {
//     debugPrint(
//         "lineupp: ${getIt<UserService>().userCredentialsNotifier.value.lineup}");
//
//     return GestureDetector(
//       onTap: () {
//         function();
//         // if (getIt<AppCache>().hasActiveSub) {
//         //   Navigator.pushNamed(context, Routes.editFoodServicesRoute);
//         // } else {
//         //   Navigator.pushNamed(context, Routes.selectPaymentPlanRoute);
//         // }
//       },
//       child: Container(
//         margin: const EdgeInsets.only(top: 12),
//         padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
//         decoration: BoxDecoration(
//             borderRadius: BorderRadius.circular(12),
//             border: Border.all(color: const Color(0xffF4F5F8))),
//         child: Row(
//           mainAxisAlignment: MainAxisAlignment.spaceBetween,
//           children: [
//             Text(
//               "Update your food plan",
//               style: AppStyles.nStyle.copyWith(
//                 fontSize: 14.sp,
//                 fontWeight: FontWeight.w700,
//               ),
//             ),
//             Container(
//               padding: const EdgeInsets.symmetric(vertical: 8, horizontal: 12),
//               decoration: BoxDecoration(
//                   color: const Color(0xffFFF3E6),
//                   borderRadius: BorderRadius.circular(8)),
//               child: Text(
//                 "Update",
//                 style: AppStyles.nStyle.copyWith(
//                     color: primaryColor,
//                     fontWeight: FontWeight.w600,
//                     fontSize: 14.sp),
//               ),
//             )
//           ],
//         ),
//       ),
//     );
//   }
// }
//
// class EmptyMealState extends StatelessWidget {
//   const EmptyMealState({
//     Key? key,
//   }) : super(key: key);
//
//   function(context) {
//     print(getIt<AppCache>().hasActiveSub.toString());
//     printWrapped(getIt<UserService>().userCredentials.toJson().toString());
//     if (getIt<AppCache>().hasActiveSub) {
//       analytics.log(
//           name: "navigationToEditFoodServicesPage",
//           params: {'success': "true"});
//       Navigator.pushNamed(context, Routes.editFoodServicesRoute);
//     } else {
//       analytics.log(
//           name: "navigationToPaymentPlanPage", params: {'success': "true"});
//       Navigator.pushNamed(context, Routes.selectPaymentPlanRoute);
//     }
//   }
//
//   // preorder(BuildContext context) async {
//   //   analytics
//   //       .log(name: "navigationToPaymentPlanPage", params: {'success': "true"});
//   //   Navigator.pushNamed(context, Routes.selectPaymentPlanRoute);
//   // }
//
//   @override
//   Widget build(BuildContext context) {
//     return Column(
//       mainAxisSize: MainAxisSize.min,
//       children: [
//         Container(
//           width: double.infinity,
//           padding: EdgeInsets.symmetric(vertical: 18.h, horizontal: 16.w),
//           decoration: BoxDecoration(
//               image: DecorationImage(
//                   image: AssetImage(
//                     "frame".png,
//                   ),
//                   fit: BoxFit.cover),
//               color: const Color(0xffF7FFE4),
//               borderRadius: BorderRadius.circular(12.r)),
//           child: Column(
//             crossAxisAlignment: CrossAxisAlignment.center,
//             children: [
//               25.sbH,
//               Text(
//                 "Don't Miss Out! Save Big.\nUp to 40% off your first order.",
//                 style: AppStyles.nStyle.copyWith(
//                     color: white, fontSize: 20.sp, fontWeight: FontWeight.w700),
//                 textAlign: TextAlign.left,
//               ),
//               14.sbH,
//               SizedBox(
//                 height: 400,
//                 child: Stack(
//                   children: [
//                     Container(
//                       height: 360,
//                       width: double.infinity,
//                       margin: EdgeInsets.only(left: 22.w, right: 22.w, top: 40),
//                       padding: EdgeInsets.symmetric(
//                           horizontal: 22.w, vertical: 18.h),
//                       decoration: BoxDecoration(
//                           color: white,
//                           borderRadius: BorderRadius.circular(12)),
//                       child: Column(
//                         children: [
//                           Text(
//                             "Weekly Plan",
//                             style: AppStyles.nStyle.copyWith(
//                                 fontSize: 18.sp, fontWeight: FontWeight.w600),
//                           ),
//                           12.sbH,
//                           Text(
//                             "2 chef-cooked meals daily\n(Lunch and Dinner from Mon. to Sun.)\nfor a week, of your choice meal.",
//                             style: AppStyles.nStyle.copyWith(
//                                 fontSize: 12.sp, fontWeight: FontWeight.w400),
//                             textAlign: TextAlign.center,
//                           ),
//                           const Divider(),
//                           ...[
//                             "Monday",
//                             "Tuesday",
//                             "Wednesday",
//                             "Thursday",
//                             "Friday",
//                             "Saturday",
//                             "Sunday"
//                           ].map((e) => Row(
//                             mainAxisAlignment:
//                             MainAxisAlignment.spaceBetween,
//                             children: [
//                               Text(e),
//                               Image.asset('check-fill'.png)
//                             ],
//                           )),
//                           12.sbH,
//                           GradientButton(
//                             text: "Order Now",
//                             onTap: () => function(context),
//                           ),
//                           8.sbH,
//                         ],
//                       ),
//                     ),
//                     Positioned(
//                         top: 5,
//                         right: 100,
//                         left: 100,
//                         child: Container(
//                           padding: const EdgeInsets.all(8),
//                           decoration: BoxDecoration(
//                               color: const Color(0xffFF0000),
//                               borderRadius: BorderRadius.circular(8),
//                               border: Border.all(color: white)),
//                           child: ValueListenableBuilder(
//                             valueListenable: uService.selectedCountry!,
//                             builder: (context, country, _) => Center(
//                               child: Text(
//                                 "£${((country.weeklyPrice != null && country.weeklyPrice != 0) ? country.weeklyPrice : "90").toString()}",
//                                 style: AppStyles.nStyle.copyWith(
//                                     color: white,
//                                     fontWeight: FontWeight.w700,
//                                     fontSize: 20.sp),
//                               ),
//                             ),
//                           ),
//                         )),
//                   ],
//                 ),
//               ),
//               12.sbH,
//               SizedBox(
//                 height: 400,
//                 child: Stack(
//                   children: [
//                     Container(
//                       height: 300,
//                       width: double.infinity,
//                       margin: EdgeInsets.only(left: 22.w, right: 22.w, top: 40),
//                       padding:
//                       EdgeInsets.only(left: 22.w, right: 22.w, top: 28.h),
//                       decoration: BoxDecoration(
//                           color: const Color(0xff560D0D).withOpacity(0.9),
//                           borderRadius: BorderRadius.circular(12)),
//                       child: Column(
//                         children: [
//                           Text(
//                             "Monthly Plan",
//                             style: AppStyles.nStyle.copyWith(
//                                 fontSize: 18.sp,
//                                 fontWeight: FontWeight.w700,
//                                 color: white),
//                           ),
//                           12.sbH,
//                           Text(
//                             "2 chef-cooked meals daily\n(Lunch and Dinner from Mon. to Sun.)\nfor a week, of your choice meal.",
//                             style: AppStyles.nStyle.copyWith(
//                                 fontSize: 12.sp,
//                                 fontWeight: FontWeight.w400,
//                                 color: white),
//                             textAlign: TextAlign.center,
//                           ),
//                           const Divider(),
//                           ...["Week 1", "Week 2", "Week 3", "Week 4"]
//                               .map((e) => Row(
//                             mainAxisAlignment:
//                             MainAxisAlignment.spaceBetween,
//                             children: [
//                               Row(
//                                 children: [
//                                   Text(
//                                     e,
//                                     style: AppStyles.nStyle.copyWith(
//                                         color: white,
//                                         fontWeight: FontWeight.w700),
//                                   ),
//                                   4.sbW,
//                                   Text(
//                                     "(Monday - Sunday)",
//                                     style: AppStyles.nStyle
//                                         .copyWith(color: white),
//                                   ),
//                                 ],
//                               ),
//                               SvgPicture.asset('check-white'.svg)
//                             ],
//                           )),
//                           12.sbH,
//                           GradientButton(
//                             text: "Order Now",
//                             onTap: () => function(context),
//                           ),
//                           16.sbH,
//                           // Text(
//                           //   "+ £10 For delivery",
//                           //   style: AppStyles.nStyle.copyWith(
//                           //       fontWeight: FontWeight.w600, color: white),
//                           // )
//                         ],
//                       ),
//                     ),
//                     Positioned(
//                         top: 5,
//                         right: 100,
//                         left: 100,
//                         child: Container(
//                           padding: const EdgeInsets.all(8),
//                           decoration: BoxDecoration(
//                               color: primaryColor,
//                               borderRadius: BorderRadius.circular(8),
//                               border: Border.all(color: white)),
//                           child: Center(
//                             child: ValueListenableBuilder(
//                               valueListenable: uService.selectedCountry!,
//                               builder: (context, country, _) => Text(
//                                 "£${((country.monthlyPrice != null && country.monthlyPrice != 0) ? country.monthlyPrice : "360").toString()}",
//                                 style: AppStyles.nStyle.copyWith(
//                                     color: white,
//                                     fontWeight: FontWeight.w700,
//                                     fontSize: 20.sp),
//                               ),
//                             ),
//                           ),
//                         )),
//                   ],
//                 ),
//               )
//             ],
//           ),
//         ),
//         24.sbH,
//         Container(
//           width: double.infinity,
//           padding: EdgeInsets.symmetric(vertical: 18.h, horizontal: 16.w),
//           decoration: BoxDecoration(
//               color: const Color(0xffFFF2EB),
//               borderRadius: BorderRadius.circular(12.r)),
//           child: Column(
//             children: [
//               Text(
//                 "Unlock 60+ \n Premium Menu",
//                 style: AppStyles.nStyle.copyWith(
//                     color: const Color(0xff125309),
//                     fontSize: 20.sp,
//                     fontWeight: FontWeight.w800),
//                 textAlign: TextAlign.center,
//               ),
//               14.sbH,
//               Container(
//                   margin: const EdgeInsets.symmetric(horizontal: 16),
//                   child: GradientButton(
//                     isLarge: true,
//                     text: "  Subscribe to a Meal Plan    ",
//                     onTap: () => function(context),
//                   )),
//               14.sbH,
//               GestureDetector(
//                 onTap: () =>
//                     Navigator.pushNamed(context, Routes.previewFoodRoute),
//                 child: GradientText(
//                   "See Trending Menus",
//                   style: AppStyles.nStyle.copyWith(
//                       color: primaryColor,
//                       fontSize: 20.sp,
//                       fontWeight: FontWeight.w800),
//                 ),
//               ),
//               18.sbH,
//               Wrap(
//                 alignment: WrapAlignment.center,
//                 children: ["rice", 'swallow soup', 'beans', 'yam', 'others']
//                     .map((e) => IntrinsicWidth(
//                   child: Container(
//                     padding: const EdgeInsets.symmetric(
//                         horizontal: 8, vertical: 8),
//                     margin: const EdgeInsets.symmetric(
//                         horizontal: 4, vertical: 6),
//                     decoration: BoxDecoration(
//                         color: const Color(0xffFFE6E4),
//                         border:
//                         Border.all(color: primaryColor, width: 1),
//                         borderRadius: BorderRadius.circular(20.r)),
//                     child: Row(
//                       children: [
//                         Image.asset(
//                           e.png,
//                           height: 24.h,
//                           width: 24.w,
//                         ),
//                         4.sbW,
//                         Text(e.capitalizeFirstOfEach),
//                       ],
//                     ),
//                   ),
//                 ))
//                     .toList(),
//               ),
//               12.sbH,
//             ],
//           ),
//         ),
//       ],
//     );
//   }
// }
//
// class GradientButton extends StatelessWidget {
//   String? text;
//   Function? onTap;
//   bool? isLarge;
//   GradientButton({super.key, this.onTap, this.text, this.isLarge = false});
//
//   @override
//   Widget build(BuildContext context) {
//     return GestureDetector(
//       behavior: HitTestBehavior.opaque,
//       onTap: () => onTap!(),
//       child: Container(
//         padding: const EdgeInsets.symmetric(vertical: 12),
//         width: double.infinity,
//         decoration: BoxDecoration(
//           borderRadius: BorderRadius.circular(8),
//           gradient: const LinearGradient(
//               colors: [Color(0xffFE7E00), Color(0xffFE0000)]),
//         ),
//         child: Center(
//           child: Text(
//             text ?? "Order Now",
//             style: AppStyles.nStyle.copyWith(
//                 color: white,
//                 fontSize: isLarge! ? 18.sp : 12.sp,
//                 fontWeight: FontWeight.w600),
//           ),
//         ),
//       ),
//     );
//   }
// }
//
// class MealShimmerPlaceHolder extends StatelessWidget {
//   const MealShimmerPlaceHolder({
//     Key? key,
//   }) : super(key: key);
//
//   @override
//   Widget build(BuildContext context) {
//     return Shimmer.fromColors(
//         baseColor: Colors.grey[300]!,
//         highlightColor: Colors.grey[100]!,
//         child: Container(
//           margin: const EdgeInsets.symmetric(vertical: 3),
//           decoration: BoxDecoration(
//               border: Border.all(color: border),
//               borderRadius: BorderRadius.circular(12)),
//           padding: const EdgeInsets.all(14),
//           child: Row(
//             children: [
//               Container(
//                   decoration: BoxDecoration(
//                       color: const Color(0xffFFE6E4),
//                       borderRadius: BorderRadius.circular(12)),
//                   child: Image.asset('food'.png)),
//               11.sbW,
//               Flexible(
//                 child: Column(
//                   crossAxisAlignment: CrossAxisAlignment.start,
//                   children: [
//                     Container(
//                       height: 10,
//                       width: double.infinity,
//                       color: Colors.black,
//                     ),
//                     4.sbH,
//                     Container(
//                       height: 10,
//                       width: double.infinity,
//                       color: Colors.black,
//                     ),
//                     4.sbH,
//                     Container(
//                       height: 10,
//                       width: double.infinity,
//                       color: Colors.black,
//                     ),
//                     4.sbH,
//                   ],
//                 ),
//               )
//             ],
//           ),
//         ));
//   }
// }
//
// class GradientText extends StatelessWidget {
//   GradientText(
//       this.text, {
//         this.gradient,
//         this.style,
//       });
//
//   final String text;
//   final TextStyle? style;
//   Gradient? gradient;
//
//   @override
//   Widget build(BuildContext context) {
//     return ShaderMask(
//       blendMode: BlendMode.srcIn,
//       shaderCallback: (bounds) => (gradient ??
//           const LinearGradient(
//               colors: [Color(0xffFE7E00), Color(0xffFE0000)]))
//           .createShader(
//         Rect.fromLTWH(0, 0, bounds.width, bounds.height),
//       ),
//       child: Text(text, style: style),
//     );
//   }
// }
