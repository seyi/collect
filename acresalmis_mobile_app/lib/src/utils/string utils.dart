import 'dart:developer' as devtools show log;

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:oktoast/oktoast.dart';

extension Log on Object {
  void log() => devtools.log(toString());
}

extension Checker on List {
  bool get isEmptyL => length == 0;
}

copyToClipBoard(String data) {
  Clipboard.setData(ClipboardData(text: data));
  showToast("Copied to Clipboard");
}

void printWrapped(String text) {
  final pattern = RegExp('.{1,800}'); // 800 is the size of each chunk
  pattern.allMatches(text).forEach((match) => debugPrint(match.group(0)));
}

String camelToSentence(String text) {
  return text.replaceAllMapped(RegExp(r'^([a-z])|[A-Z]'),
      (Match m) => m[1] == null ? " ${m[0]}" : m[1]!.toUpperCase());
}

String splitCamelCase(String input) {
  // Use regular expression to find uppercase letters that are preceded by a lowercase letter or another uppercase letter
  final RegExp exp = RegExp(r'(?<=[a-z])(?=[A-Z])');

  // Split the input string based on the regular expression
  final List<String> parts = input.split(exp);

  // Join the parts with a space and return the result
  return parts.join(' ');
}
