import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:google_fonts/google_fonts.dart';

class GenericBottomsheetList extends StatefulWidget {
  List<dynamic> list;
  final String title;
  Function(dynamic)? onTap;
  dynamic? old;
  GenericBottomsheetList({Key? key, this.onTap, this.old, required this.list, required this.title}) : super(key: key);

  @override
  State<GenericBottomsheetList> createState() => _GenericBottomsheetListState();
}

class _GenericBottomsheetListState extends State<GenericBottomsheetList> {
  ValueNotifier<dynamic?> selected = ValueNotifier(null);
  @override
  void initState() {
    super.initState();
    if (widget.old != null) {
      selected = ValueNotifier(widget.old!);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.only(top: 70.h),
      child: ClipRRect(
        borderRadius: const BorderRadius.only(topLeft: Radius.circular(20), topRight: Radius.circular(20)),
        child: SingleChildScrollView(
          child: Container(
            padding: EdgeInsets.only(
                // right: 20,
                // left: 20,
                bottom: MediaQuery.of(context).viewInsets.bottom),
            color: Colors.white,
            // height: 500,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 20.0),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  12.sbH,
                  12.0.sbH,
                  Align(
                    alignment: Alignment.centerLeft,
                    child: Text(
                      widget.title,
                      style: GoogleFonts.plusJakartaSans(fontWeight: FontWeight.w700, fontSize: 24.sp),
                    ),
                  ),
                  24.0.sbH,
                  ...widget.list
                      .map((e) => GestureDetector(
                            behavior: HitTestBehavior.opaque,
                            onTap: () {
                              selected.value = e;
                              widget.onTap!(selected.value!);
                              Navigator.pop(context);
                            },
                            child: Padding(
                              padding: EdgeInsets.symmetric(vertical: 12.h),
                              child: Row(
                                children: [
                                  12.sbW,
                                  Text(
                                    e.toString().capitalizeFirstOfEach,
                                    style: GoogleFonts.plusJakartaSans(fontWeight: FontWeight.w700, fontSize: 16.sp),
                                  ),
                                  const Spacer(),
                                  ValueListenableBuilder(
                                      valueListenable: selected, builder: (context, currency_, _) => currency_ == e ? SvgPicture.asset('radio-a'.svg) : SvgPicture.asset('radio'.svg))
                                ],
                              ),
                            ),
                          ))
                      .toList(),
                  32.sbH,
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}

showGenericSheet(BuildContext context, {Function(dynamic)? onTap, dynamic? old, required String title, required List<dynamic> list}) {
  showModalBottomSheet(
      enableDrag: true,
      isDismissible: true,
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) {
        return GenericBottomsheetList(
          onTap: onTap,
          old: old,
          list: list,
          title: title,
        );
      });
}
