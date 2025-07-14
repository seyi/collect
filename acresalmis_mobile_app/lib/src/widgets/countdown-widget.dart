import 'dart:async';

import 'package:flutter/material.dart';

class CountdownWidget extends StatefulWidget {
  final DateTime targetTime;
  final Widget Function(BuildContext context, Duration timeRemaining) builder;

  const CountdownWidget({
    Key? key,
    required this.targetTime,
    required this.builder,
  }) : super(key: key);

  @override
  State<CountdownWidget> createState() => _CountdownWidgetState();
}

class _CountdownWidgetState extends State<CountdownWidget> {
  late Timer _timer;
  late Duration _timeRemaining;

  @override
  void initState() {
    super.initState();
    _updateTime();
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (!mounted) return; // Prevents calling setState if widget is disposed
      _updateTime();
    });
  }

  void _updateTime() {
    final newTime = widget.targetTime.difference(DateTime.now());
    if (newTime.isNegative) {
      _timer.cancel();
      setState(() {
        _timeRemaining = Duration.zero;
      });
    } else {
      setState(() {
        _timeRemaining = newTime;
      });
    }
  }

  @override
  void dispose() {
    _timer.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return widget.builder(context, _timeRemaining);
  }
}
