import 'dart:convert';

LoginResponse loginModelFromJson(String str) => LoginResponse.fromJson(json.decode(str));
String loginModelToJson(LoginResponse data) => json.encode(data.toJson());

String loginModelDataToString(dynamic data) => json.encode(data);
dynamic loginModelDataToJson(String data) => json.decode(data);

class LoginResponse {
  bool? status;
  LoginRes? data;
  String? message;

  bool get success => status == true;

  LoginResponse({this.status, this.data, this.message});

  LoginResponse.fromJson(Map<String, dynamic> json) {
    status = json['status'];
    message = json['data']['message'];
    data = json['data'] != null ? LoginRes.fromJson(json['data']) : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['status'] = status;
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    return data;
  }
}

class LoginRes {
  String? accessToken;
  String? tokenType;
  String? usertoken;
  ACUser? user;

  LoginRes({this.accessToken, this.tokenType, this.user, this.usertoken});

  LoginRes.fromJson(Map<String, dynamic> json) {
    accessToken = json['access_token'];
    tokenType = json['token_type'];
    usertoken = json['usertoken'];
    user = json['user'] != null ? ACUser.fromJson(json['user']) : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['access_token'] = accessToken;
    data['token_type'] = tokenType;
    data['usertoken'] = usertoken;
    if (user != null) {
      data['user'] = user!.toJson();
    }
    return data;
  }
}

class ACUser {
  num? id;
  String? name;
  String? email;
  String? avatar;
  String? picture;
  num? balance;
  num? commission;
  String? phone;
  num? emailStatus;
  num? appStatus;
  String? affliateCode;
  String? refer;
  num? status;
  num? type;
  String? emailVerifiedAt;
  String? createdAt;
  String? updatedAt;
  String? tag;
  Usertag? usertag;

  ACUser(
      {this.id,
      this.name,
      this.email,
      this.avatar,
      this.picture,
      this.balance,
      this.commission,
      this.phone,
      this.emailStatus,
      this.appStatus,
      this.affliateCode,
      this.refer,
      this.status,
      this.type,
      this.emailVerifiedAt,
      this.createdAt,
      this.updatedAt,
      this.tag,
      this.usertag});

  ACUser.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    email = json['email'];
    avatar = json['avatar'];
    picture = json['picture'];
    balance = json['balance'];
    commission = json['commission'];
    phone = json['phone'];
    emailStatus = json['email_status'];
    appStatus = json['app_status'];
    affliateCode = json['affliate_code'];
    refer = json['refer'];
    status = json['status'];
    type = json['type'];
    emailVerifiedAt = json['email_verified_at'];
    createdAt = json['created_at'];
    updatedAt = json['updated_at'];
    tag = json['tag'];
    if (json['usertag'] != null) {
      usertag = Usertag.fromJson(json['usertag']);
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['id'] = id;
    data['name'] = name;
    data['email'] = email;
    data['avatar'] = avatar;
    data['picture'] = picture;
    data['balance'] = balance;
    data['commission'] = commission;
    data['phone'] = phone;
    data['email_status'] = emailStatus;
    data['app_status'] = appStatus;
    data['affliate_code'] = affliateCode;
    data['refer'] = refer;
    data['status'] = status;
    data['type'] = type;
    data['email_verified_at'] = emailVerifiedAt;
    data['created_at'] = createdAt;
    data['updated_at'] = updatedAt;
    data['tag'] = tag;
    data['usertag'] = usertag?.toJson();
    return data;
  }
}

class Usertag {
  int? id;
  String? tag;
  int? userId;
  String? createdAt;
  String? updatedAt;

  Usertag({this.id, this.tag, this.userId, this.createdAt, this.updatedAt});

  Usertag.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    tag = json['tag'];
    userId = json['user_id'];
    createdAt = json['created_at'];
    updatedAt = json['updated_at'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['id'] = id;
    data['tag'] = tag;
    data['user_id'] = userId;
    data['created_at'] = createdAt;
    data['updated_at'] = updatedAt;
    return data;
  }
}
