import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:shimmer/shimmer.dart';

class CircularCachedNetworkImage extends StatelessWidget {
  final String? imageUrl;
  final double size;
  final String placeholderAsset;

  const CircularCachedNetworkImage({
    super.key,
    required this.imageUrl,
    this.size = 50.0,
    this.placeholderAsset = 'assets/images/avatar.png',
  });

  @override
  Widget build(BuildContext context) {
    return ClipOval(
      child: SizedBox(
        height: size,
        width: size,
        child: CachedNetworkImage(
          imageUrl: imageUrl ?? '',
          fit: BoxFit.cover,
          placeholder: (context, url) => Shimmer.fromColors(
            baseColor: Colors.grey.shade300,
            highlightColor: Colors.grey.shade100,
            child: Container(
              color: Colors.grey.shade300,
            ),
          ),
          errorWidget: (context, url, error) => Image.asset(
            placeholderAsset,
            fit: BoxFit.cover,
          ),
        ),
      ),
    );
  }
}
