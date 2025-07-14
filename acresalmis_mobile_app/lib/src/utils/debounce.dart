import 'dart:async';

import 'package:flutter/foundation.dart';

class Debouncer {
  Debouncer({this.delay = const Duration(milliseconds: 300)});

  final Duration delay;
  Timer? _timer;

  void call(void Function() callback) {
    _timer?.cancel();
    _timer = Timer(delay, callback);
  }

  void dispose() {
    _timer
        ?.cancel(); // You can comment-out this line if you want. I am not sure if this call brings any value.
    _timer = null;
  }
}

class DebounceRequest {
  Timer? _timer;
  void run(VoidCallback action, {int? milliSeconds}) {
    if (_timer?.isActive ?? false) {
      _timer?.cancel();
    }
    _timer = Timer(Duration(milliseconds: milliSeconds ?? 1000), action);
  }
}

final debounce = DebounceRequest();
debounceThis(VoidCallback v, {int? milliSeconds}) {
  debounce.run(() {
    v();
  }, milliSeconds: milliSeconds);
}

extension DebounceExtension on Function {
  debounceIt({int? milliseconds}) {
    debounceThis(() => this(), milliSeconds: milliseconds);
  }
}

extension DebounceFunctionExtension on VoidCallback {
  static final _timers = <Function, Timer>{};
  static final _timers1 = <Function, Timer>{};

  void debounceFunction(
      {Duration duration = const Duration(milliseconds: 5000)}) {
    if (_timers.containsKey(this)) {
      _timers[this]!.cancel();
    }
    _timers[this] = Timer(duration, () {
      this();
      _timers.remove(this);
    });
  }

  void debounceFunction1(
      {Duration duration = const Duration(milliseconds: 5000)}) {
    if (_timers1.containsKey(this)) {
      _timers1[this]!.cancel();
    }
    _timers1[this] = Timer(duration, () {
      this();
      _timers1.remove(this);
    });
  }
}
