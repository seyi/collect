class NotificationModel {
  NotificationRes? data;
  int? statusCode;
  String? status;

  NotificationModel({this.data, this.statusCode, this.status});

  NotificationModel.fromJson(Map<String, dynamic> json) {
    data = json['data'] != null ? NotificationRes.fromJson(json['data']) : null;
    statusCode = json['statusCode'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    data['statusCode'] = statusCode;
    data['status'] = status;
    return data;
  }
}

class NotificationRes {
  int? totalCount;
  List<NotificationData>? data;

  NotificationRes({this.totalCount, this.data});

  NotificationRes.fromJson(Map<String, dynamic> json) {
    totalCount = json['totalCount'];
    if (json['data'] != null) {
      data = <NotificationData>[];
      json['data'].forEach((v) {
        data!.add(NotificationData.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['totalCount'] = totalCount;
    if (this.data != null) {
      data['data'] = this.data!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class NotificationData {
  String? sId;
  String? tag;
  String? title;
  String? customer;
  String? content;
  String? status;
  String? createdAt;
  String? updatedAt;
  int? iV;

  NotificationData(
      {this.sId,
      this.tag,
      this.title,
      this.customer,
      this.content,
      this.status,
      this.createdAt,
      this.updatedAt,
      this.iV});

  NotificationData.fromJson(Map<String, dynamic> json) {
    sId = json['_id'];
    tag = json['tag'];
    title = json['title'];
    customer = json['customer'];
    content = json['content'];
    status = json['status'];
    createdAt = json['createdAt'];
    updatedAt = json['updatedAt'];
    iV = json['__v'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['_id'] = sId;
    data['tag'] = tag;
    data['title'] = title;
    data['customer'] = customer;
    data['content'] = content;
    data['status'] = status;
    data['createdAt'] = createdAt;
    data['updatedAt'] = updatedAt;
    data['__v'] = iV;
    return data;
  }
}
