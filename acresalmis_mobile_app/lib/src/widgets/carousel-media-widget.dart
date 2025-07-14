import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:shimmer/shimmer.dart';

class MediaWidget extends StatefulWidget {
  final String mediaUrl;
  final String defaultImageAsset;

  MediaWidget({
    required this.mediaUrl,
    this.defaultImageAsset = 'assets/images/g1.png',
  });

  @override
  _MediaWidgetState createState() => _MediaWidgetState();
}

class _MediaWidgetState extends State<MediaWidget> {
  ValueNotifier<bool> isLoading = ValueNotifier(false);

  bool _isYoutubeUrl(String url) {
    return url.contains('youtube.com') || url.contains('youtu.be');
  }

  bool _isYoutubeShortUrl(String url) {
    return url.contains('youtube.com/shorts/');
  }

  bool _isImageUrl(String url) {
    return url.endsWith('.png') || url.endsWith('.jpg') || url.endsWith('.jpeg') || url.endsWith('.gif');
  }

  String? _getYoutubeEmbedUrl(String url) {
    if (url.contains('youtube.com/watch')) {
      final uri = Uri.parse(url);
      final videoId = uri.queryParameters['v'];
      return videoId != null ? 'https://www.youtube.com/embed/$videoId' : null;
    } else if (url.contains('youtu.be')) {
      final videoId = url.split('/').last;
      return 'https://www.youtube.com/embed/$videoId';
    } else if (url.contains('youtube.com/shorts')) {
      final videoId = url.split('/').last;
      return 'https://www.youtube.com/embed/$videoId'; // Shorts can be embedded like this
    }
    return null;
  }

  @override
  Widget build(BuildContext context) {
    if (_isImageUrl(widget.mediaUrl)) {
      // Display image
      return ClipRRect(
        borderRadius: BorderRadius.circular(12),
        child: CachedNetworkImage(
          width: double.infinity,
          height: double.infinity,
          imageUrl: widget.mediaUrl,
          fit: BoxFit.fill,
          placeholder: (context, url) => Shimmer.fromColors(
            baseColor: Colors.grey[300]!,
            highlightColor: Colors.grey[100]!,
            child: Container(
              color: Colors.grey[300],
            ),
          ),
          errorWidget: (context, url, error) => Image.asset(
            widget.defaultImageAsset,
            fit: BoxFit.fill,
          ),
        ),
      );
    } else if (_isYoutubeUrl(widget.mediaUrl) || _isYoutubeShortUrl(widget.mediaUrl)) {
      // Display YouTube video or Shorts using WebView
      String? embedUrl = _getYoutubeEmbedUrl(widget.mediaUrl);

      return embedUrl != null
          ? ValueListenableBuilder(
              valueListenable: isLoading,
              builder: (context, loading, _) => ClipRRect(
                borderRadius: BorderRadius.circular(12),
                child: Stack(
                  children: [
                    // ClipRRect(
                    //   borderRadius: BorderRadius.circular(12),
                    //   child: WebView(
                    //     initialUrl: embedUrl,
                    //     javascriptMode: JavascriptMode.unrestricted,
                    //     onPageStarted: (v) {
                    //       isLoading.value = true;
                    //     },
                    //     onPageFinished: (v) {
                    //       isLoading.value = false;
                    //     },
                    //   ),
                    // ),
                    if (loading)
                      Positioned.fill(
                          child: ClipRRect(
                        borderRadius: BorderRadius.circular(12),
                        child: Shimmer.fromColors(
                          baseColor: Colors.grey[300]!,
                          highlightColor: Colors.grey[100]!,
                          child: Container(
                            color: Colors.grey[300],
                          ),
                        ),
                      ))
                  ],
                ),
              ),
            )
          : const Center(child: Text('Invalid YouTube or Shorts link'));
    } else {
      // If the URL is neither an image nor a YouTube link, return a default widget
      return const Center(
        child: Text('Unsupported media format'),
      );
    }
  }
}
