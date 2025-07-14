import 'dart:convert';

import 'package:flutter_template/src/model/login-response.dart';

GetUserResponse userModelFromJson(String str) => GetUserResponse.fromJson(json.decode(str));
String userModelToJson(GetUserResponse data) => json.encode(data.toJson());

String userModelDataToString(dynamic data) => json.encode(data);
dynamic userModelDataToJson(String data) => json.decode(data);

class GetUserResponse {
  ACUser? data;
  int? statusCode;
  bool? status;
  String? message;
  // bool? success;
  GetUserResponse({
    this.data,
    this.statusCode,
    this.status,
    this.message,
    // this.success = false
  });

  bool get success => status == true;

  GetUserResponse.fromJson(Map<String, dynamic> json) {
    data = json['data'] != null ? ACUser.fromJson(json['data']) : null;
    statusCode = json['statusCode'];
    status = json['status'];
    message = json['message'];
    // success = statusCode == 200 || statusCode == 201;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    data['statusCode'] = statusCode;
    data['status'] = status;
    data['message'] = message;
    // data['success'] = success;
    return data;
  }
}
