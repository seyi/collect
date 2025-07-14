import 'package:flutter/material.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:shimmer/shimmer.dart';

Widget shimmerWidget({double? height, double? width}) => Shimmer.fromColors(
    baseColor: Colors.grey[300]!,
    highlightColor: Colors.grey[100]!,
    child: Container(
        margin: const EdgeInsets.symmetric(vertical: 3),
        decoration: BoxDecoration(border: Border.all(color: border), borderRadius: BorderRadius.circular(12)),
        padding: const EdgeInsets.all(14),
        child: Container(
          height: height,
          width: width ?? 100,
          color: Colors.black,
        )));
