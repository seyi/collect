import 'package:flutter/material.dart';

Color getStatusColor(String status) {
  for (var e in OrderStatus.values) {
    if (e.value == status) {
      return e.color!;
    }
  }
  return Colors.orangeAccent;
}

enum OrderStatus {
  processing(
      title: "Pending",
      value: "processing",
      color: Color(0xFFFE7E00),
      position: 0),
  paid(
      title: "Paid",
      value: "payment_received",
      color: Color(0xff32A071),
      position: 1),
  confirming(
      title: "Confirming",
      value: "confirming",
      color: Color(0xff2B3499),
      position: 2),
  accepted(
      title: "Accepted",
      value: "accepted",
      color: Color(0xFF2962FF),
      position: 3),
  dispatched(
      title: "Dispatched",
      value: "dispatched",
      color: Color(0xff4CAF50),
      position: 4),
  received(
      title: "Received",
      value: "received",
      color: Color(0xff2E7D32),
      position: 5),
  // delivered
  delivered(
      title: "Delivered",
      value: "delivered",
      color: Color(0xff2E7D32),
      position: 5),
  none(
      title: "Pending",
      value: "deactivated",
      color: Color(0xFFFE7E00),
      position: 6),
  cancelled(title: "CANCELLED", value: "cancelled");

  final String? title;
  final String? value;
  final Color? color;
  final num? position;
  const OrderStatus({this.title, this.value, this.color, this.position});
}
