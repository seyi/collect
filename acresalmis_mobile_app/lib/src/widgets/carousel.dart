import 'dart:async';

import 'package:carousel_slider/carousel_slider.dart';
import 'package:flutter/material.dart';
import 'package:flutter_template/src/widgets/carousel-media-widget.dart';

class ImageCarousel2 extends StatefulWidget {
  final List<dynamic> imageUrls;
  final Duration slideDuration;
  final String defaultImageAsset;

  ImageCarousel2({
    required this.imageUrls,
    this.slideDuration = const Duration(seconds: 5),
    this.defaultImageAsset = 'assets/images/g1.png',
  });

  @override
  _ImageCarousel2State createState() => _ImageCarousel2State();
}

class _ImageCarousel2State extends State<ImageCarousel2> {
  late PageController _pageController;
  int _currentPage = 0;
  late Timer _timer;

  @override
  void initState() {
    super.initState();

    if (widget.imageUrls.isNotEmpty) {
      _pageController = PageController(initialPage: 0, viewportFraction: 0.85);
      _startTimer();
    } else {}
  }

  _startTimer() {
    _timer = Timer.periodic(widget.slideDuration, (Timer timer) {
      if (_currentPage < widget.imageUrls.length - 1) {
        _currentPage++;
      } else {
        _currentPage = 0;
      }

      _pageController.animateToPage(
        _currentPage,
        duration: const Duration(milliseconds: 350),
        curve: Curves.easeIn,
      );
    });
  }

  @override
  void dispose() {
    super.dispose();
    _timer.cancel();
    _pageController.dispose();
  }

  List<String> links = ['https://youtu.be/mv_4mxUSpWI?si=WTXBRj3sqDXdLNLk', "https://youtube.com/shorts/4r-goobwB7g?si=TFcCzF_HqdzVMC_I"];

  int initialPage = 0;
  final CarouselSliderController _controller = CarouselSliderController();

  @override
  Widget build(BuildContext context) {
    return Visibility(
      visible: widget.imageUrls.isNotEmpty,
      child: SizedBox(
        height: 120,
        child: CarouselSlider(
            carouselController: _controller,
            items: widget.imageUrls.map((image) {
              return Builder(builder: (BuildContext context) {
                return GestureDetector(
                  behavior: HitTestBehavior.opaque,
                  onTap: () {},
                  child: MediaWidget(mediaUrl: image.photo ?? "", defaultImageAsset: widget.defaultImageAsset),
                );
              });
            }).toList(),
            options: CarouselOptions(
              height: 200.0,
              viewportFraction: 0.6,
              autoPlay: true,
              autoPlayInterval: const Duration(seconds: 6),
              enlargeCenterPage: true,
              onPageChanged: (index, reason) {
                setState(() {
                  initialPage = index;
                });
              },
            )),
      ),
    );
  }
}
