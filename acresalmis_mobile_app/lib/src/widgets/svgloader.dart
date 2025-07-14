import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_svg/flutter_svg.dart';

Future<AssetImage> loadAssetImageWithFallback(
    String assetPath, String fallbackAssetPath) async {
  try {
    // Try to load the specified asset
    await rootBundle.load(assetPath);
    return AssetImage(assetPath);
  } catch (e) {
    // If there's an error (asset not found), load the fallback asset
    return AssetImage(fallbackAssetPath);
  }
}

class SvgLoader extends StatelessWidget {
  final String svgPath;
  final String defaultSvgPath;

  SvgLoader({required this.svgPath, required this.defaultSvgPath});

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<bool>(
      future: _checkSvgExists(svgPath),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.done) {
          if (snapshot.data == true) {
            return SvgPicture.asset(svgPath);
          } else {
            return SvgPicture.asset(defaultSvgPath);
          }
        } else {
          return CircularProgressIndicator();
        }
      },
    );
  }

  Future<bool> _checkSvgExists(String path) async {
    try {
      // Attempt to load the SVG to check if it exists
      await precachePicture(
        ExactAssetPicture(SvgPicture.svgStringDecoderBuilder, path),
        null,
      );
      return true;
    } catch (e) {
      return false;
    }
  }
}
