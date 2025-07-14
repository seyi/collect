import 'package:flutter/material.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:webview_flutter/webview_flutter.dart';

String callback = "";

class WebViewSheet extends StatefulWidget {
  final Function() onDone;
  final String url;
  const WebViewSheet({super.key, required this.onDone, required this.url});

  @override
  State<WebViewSheet> createState() => _WebViewSheetState();
}

class _WebViewSheetState extends State<WebViewSheet> {
  late WebViewController _controller = WebViewController();

  @override
  void initState() {
    super.initState();
    _controller = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setNavigationDelegate(
        NavigationDelegate(
          onProgress: (int progress) {
            // Update loading bar.
          },
          onPageStarted: (String url) {},
          onPageFinished: (String url) {},
          onHttpError: (HttpResponseError error) {},
          onWebResourceError: (WebResourceError error) {},
          onNavigationRequest: (NavigationRequest request) {
            if (request.url.startsWith('https://www.youtube.com/')) {
              return NavigationDecision.prevent;
            }
            return NavigationDecision.navigate;
          },
        ),
      )
      ..loadRequest(Uri.parse(url));
  }

  String get url => widget.url;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        elevation: 0,
        backgroundColor: white,
        automaticallyImplyLeading: true,
        toolbarHeight: 30,
        leading: GestureDetector(
          onTap: () => Navigator.pop(context),
          behavior: HitTestBehavior.opaque,
          child: const Icon(
            Icons.close,
            color: AppColors.main,
          ),
        ),
      ),
      body: SingleChildScrollView(
        child: Container(
          height: height(context),
          padding: EdgeInsets.only(
              // right: 20,
              // left: 20,
              bottom: MediaQuery.of(context).viewInsets.bottom),
          color: Colors.white,
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 18.0),
            child: Column(mainAxisSize: MainAxisSize.min, crossAxisAlignment: CrossAxisAlignment.start, children: [
              12.0.sbH,
              Expanded(
                  child: WebViewWidget(
                controller: _controller,
              ))
            ]),
          ),
        ),
      ),
    );
  }
}
