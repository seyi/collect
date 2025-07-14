import 'dart:io';

import 'package:intl/intl.dart';
import 'package:timezone/data/latest.dart' as tz; // For handling time zones
import 'package:url_launcher/url_launcher.dart' show launchUrl;
import 'package:url_launcher/url_launcher_string.dart';

RegExp _numeric = RegExp(r'^-?[0-9]+$');

/// check if the string contains only numbers
bool isNumeric(String str) {
  return _numeric.hasMatch(str);
}

const ext = 0;
final formatCurrency = NumberFormat.simpleCurrency(locale: Platform.localeName, name: 'NGN');
final nairaCurrency = NumberFormat.simpleCurrency(locale: Platform.localeName, name: 'NGN');

String getCurrencySign(String name) {
  return NumberFormat.simpleCurrency(name: name.toUpperCase()).currencySymbol;
}

//Formats the amount and returns a formatted amount
String formatPrice(String amount) {
  return nairaCurrency.format(num.parse(amount)).toString();
}

extension StringFormatExtens on String? {
  String formatDate() => DateFormat('dd/MM/yyyy').format(DateTime.parse(this!
      // "2024-10-17T14:23:22.956Z"
      ));
}

extension StringCasingExtension on String {
  bool isWithinLast24Hours() {
    // Initialize timezone package
    tz.initializeTimeZones();

    try {
      // Parse the string to DateTime object (assuming it's in UTC)
      DateTime parsedDate = DateTime.parse(this);

      // Get current date in UTC
      DateTime now = DateTime.now().toUtc();

      // Calculate the difference in days
      Duration difference = now.difference(parsedDate);

      // Check if it's not more than 24 hours ago
      return difference.inHours <= 24;
    } catch (e) {
      // If there's an error in parsing the date, return false
      return false;
    }
  }

  int daysLeftTo30() {
    // Initialize timezone package
    tz.initializeTimeZones();

    try {
      // Parse the string to a DateTime object (assuming the date is in UTC)
      DateTime parsedDate = DateTime.parse(this);

      // Get current date in UTC
      DateTime now = DateTime.now().toUtc();

      // Calculate the difference in days between the current date and the given date
      Duration difference = now.difference(parsedDate);

      // Total days in the window (30 days)
      int daysTo30 = 30;

      // Calculate how many days are left to complete 30 days
      int daysLeft = daysTo30 - difference.inDays;

      // If daysLeft is negative, it means the 30 days have already passed, so return 0
      return daysLeft > 0 ? daysLeft : 0;
    } catch (e) {
      // If there's an error in parsing the date, return -1 to indicate failure
      return -1;
    }
  }

  String? validationMessage() => "Please enter a valid $this";

  String? camelCase() => toBeginningOfSentenceCase(this);
  String toCapitalized() => length > 0 ? '${this[0].toUpperCase()}${substring(1).toLowerCase()}' : '';
  String toTitleCase() => replaceAll(RegExp(' +'), ' ').split(' ').map((str) => str.toCapitalized()).join(' ');
  String? trimToken() => contains(":") ? split(":")[1].trim() : this;
  String? trimSpaces() => replaceAll(" ", "");
  String? stripInternationalNumbers() => isNotEmpty
      ? contains("+234")
          ? replaceAll("+234", "0")
          : this
      : "";

  String getInitials() => isNotEmpty ? trim().split(RegExp(' +')).map((s) => s[0].toUpperCase()).take(2).join() : '';
  String getNumbers() => isNotEmpty ? replaceAll(RegExp('[^0-9]'), '').replaceAll("-", "") : "";
  String getAlphabets() => isNotEmpty ? replaceAll(RegExp('[^A-Za-z]'), '').replaceAll("-", "") : "";
  String getAlphaNumeric() => isNotEmpty ? replaceAll(RegExp('[^A-Za-z0-9]'), '').replaceAll("-", "") : "";
  String? trimLength(int startRange) => replaceRange(startRange, length, ".");
  String get inCaps => length > 0 ? '${this[0].toUpperCase()}${this.substring(1)}' : '';
  String get capitalizeFirstOfEach => replaceAll(RegExp(' +'), ' ').split(" ").map((str) => str.inCaps).join(" ");
}

extension ImagePath on String {
  String get svg => 'assets/images/$this.svg';
  String get png => 'assets/images/$this.png';
  String get cPng => 'assets/images/countries/$this.png';
  String get jpg => 'assets/images/$this.jpg';
}

extension ExtraStringMethods on String? {
  /// Returns `true` if this string is not `null` and not empty.
  bool get isNotNullNorEmpty {
    return this?.isNotEmpty ?? false;
  }

  String capitalizeFirstLetter() {
    if ((this ?? "").isEmpty) return (this ?? ""); // If the string is empty, return it as is
    return (this ?? "")[0].toUpperCase() + (this ?? "").substring(1);
  }

  bool get hasValue => this != null && this!.isNotEmpty;
  void toast({bool success = false}) => {};
}

extension NullCheckerExtensions on num? {
  /// Returns `true` if this num is not `null` and not empty.
  bool get isNotNullNorEmpty {
    return this != null;
  }

  bool get hasValue => this != null && this != 0;
}

extension NumExtensions on int {
  num addPercentage(num v) => this + ((v / 100) * this);
  num getPercentage(num v) => ((v / 100) * this);
}

extension NumExtensionss on num {
  num addPercentage(num v) => (this ?? 0) + ((v / 100) * (this ?? 0));
  num getPercentage(num v) => ((v / 100) * (this ?? 0));
  DateTime mToDate() => DateTime.fromMillisecondsSinceEpoch(toInt());
}

void openUrl({String? url}) {
  launchUrl(Uri.parse("http://$url"));
}

void openUrlExternal({String? url}) {
  launchUrl(Uri.parse(url ?? "https://eatflutter_template.com"), mode: LaunchMode.externalApplication);
}

void openMailApp({String? receiver, String? title, String? body}) {
  launchUrl(Uri.parse("mailto:$receiver?subject=$title&body=$body"));
}

void makeCall({String? number}) {
  launchUrl(Uri.parse("tel:$number"));
}

extension Ex on double {
  double toPrecision(int n) => double.parse(toStringAsFixed(n));
}

extension DateUtils on DateTime {
  bool isExpired() => isBefore(DateTime.now());
  String formatDate({String? format = 'MM/dd/yyyy, hh:mm a'}) => DateFormat(format).format(this);

//  Output = 03/03/2022, 08:04 AM
}

String replaceFirstAndLast(String input, String i, String v) {
  if (input.isEmpty || i.isEmpty || v.isEmpty) {
    throw ArgumentError('Input string and replacements must not be empty.');
  }

  String result = input.substring(1, input.length - 1); // Get substring excluding first and last character

  // Replace first character with first character of firstReplacement
  result = i[0] + result;

  // Replace last character with first character of lastReplacement
  result = result + v[0];

  return result;
}
