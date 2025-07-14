import 'dart:convert';

ResModel resModelFromJson(String str) => ResModel.fromJson(json.decode(str));
String resModelToJson(ResModel data) => json.encode(data.toJson());

String resModelDataToString(dynamic data) => json.encode(data);
dynamic resModelDataToJson(String data) => json.decode(data);

class ResModel {
  dynamic data;
  int? statusCode;
  bool? status;
  String? message;
  bool? success;
  bool? down;
  String? info;

  ResModel({this.data, this.statusCode, this.status = false, this.message, this.info, this.down, this.success = false});

  ResModel.fromJson(Map<String, dynamic> json) {
    data = json['data'];
    statusCode = json['statusCode'];
    status = json['status'];
    message = json['Message'] ?? json['data']['message'] ?? json['message'];
    info = json['info'];
    down = json['down'];
    success = statusCode == 200 || statusCode == 201;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    data['statusCode'] = statusCode;
    data['status'] = status;
    data['message'] = message;
    data['down'] = down;
    data['success'] = success;
    data['info'] = info;
    return data;
  }

  @override
  String toString() {
    return 'ResModel{data: ${data.toString()}, statusCode: $statusCode, status: $status, message: $message, success: $success, info:$info, down:$down}';
  }

  ResModel copyWith({
    dynamic data,
    int? statusCode,
    bool? status,
    bool? down,
    String? message,
    bool? success,
    String? info,
  }) {
    return ResModel(
        data: data ?? this.data,
        statusCode: statusCode ?? this.statusCode,
        status: status ?? this.status,
        message: message ?? this.message,
        success: success ?? this.success,
        info: info ?? this.info,
        down: down ?? this.down);
  }
}
