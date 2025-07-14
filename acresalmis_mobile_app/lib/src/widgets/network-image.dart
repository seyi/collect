import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:shimmer/shimmer.dart';

class NetworkImageWithPlaceholder extends StatelessWidget {
  final String imageUrl;
  final double? height;
  final double? width_;

  const NetworkImageWithPlaceholder({
    Key? key,
    this.height,
    this.width_,
    required this.imageUrl,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return CachedNetworkImage(
      imageUrl: imageUrl,
      height: height ?? 70.h,
      width: width_ ?? (width(context) / 2.5),
      fit: BoxFit.fill,
      placeholder: (context, url) => Shimmer.fromColors(
        baseColor: Colors.grey[600]!,
        highlightColor: Colors.grey[500]!,
        child: Container(
          color: Colors.grey,
        ),
      ),
      errorWidget: (context, url, error) => Container(
        color: Colors.grey,
        child: const Icon(
          Icons.error,
          color: Colors.red,
        ),
      ),
      // fit: BoxFit.cover,
    );
  }
}
