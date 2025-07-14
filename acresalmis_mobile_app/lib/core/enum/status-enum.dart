import 'package:flutter/material.dart';

enum Status {
  verified(title: "Verified", color: Color(0XFF4FBBA8)),
  pending(title: "Pending", color: Color(0XFFD2A447)),
  unverified(title: "Unverified", color: Colors.grey),
  rejected(title: "Rejected", color: Color(0XFFE86969)),
  review(title: "In review", color: Colors.lightBlueAccent);

  final Color? color;
  final String? title;
  const Status({this.title, this.color});
}

enum IdTypeEnum {
  none(title: ""),
  nin(title: "NIN"),
  internationalPassport(title: "INTERNATIONAL PASSPORT"),
  driversLicense(title: "DRIVER LICENSE");

  final String? title;
  const IdTypeEnum({this.title});
}

extension IdTypeExtension on String {
  IdTypeEnum toIdType() {
    switch (toLowerCase()) {
      case "nin":
        return IdTypeEnum.nin;
      case "international passport":
        return IdTypeEnum.internationalPassport;
      case "driver license":
        return IdTypeEnum.driversLicense;
      default:
        return IdTypeEnum.none;
    }
  }
}

Color color = Colors.lightBlueAccent;
