import 'dart:convert';

import 'package:flutter/cupertino.dart';

CountriesResponse countriesModelFromJson(String str) =>
    CountriesResponse.fromJson(json.decode(str));
String countriesModelToJson(CountriesResponse data) =>
    json.encode(data.toJson());

String countriesModelDataToString(dynamic data) => json.encode(data);
dynamic countriesModelDataToJson(String data) => json.decode(data);

class CountriesResponse {
  ICountry? data;
  String? version;
  int? statusCode;
  String? status;
  String? message;
  bool? success;

  ValueNotifier<CountryRes> defaultCountry = ValueNotifier(CountryRes(
      sId: "65d06d112421a196fc6e199e", name: 'nigeria', code: "NGN"));

  CountriesResponse(
      {this.data,
      this.version,
      this.statusCode,
      this.status,
      this.message,
      this.success = false});

  CountriesResponse.fromJson(Map<String, dynamic> json) {
    if (json['data'] != null) {
      data = ICountry.fromJson(json['data']);
    }

    version = json['version'];
    statusCode = json['statusCode'];
    status = json['status'];
    success = statusCode == 200 || statusCode == 201;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    if (this.data != null) {
      data['data'] = data; //this.data!.map((v) => v.toJson()).toList();
    }
    data['version'] = version;
    data['statusCode'] = statusCode;
    data['status'] = status;
    return data;
  }
}

class ICountry {
  List<CountryRes>? data;
  int? totalCount;

  ICountry({this.totalCount, this.data});

  ICountry.fromJson(Map<String, dynamic> json) {
    if (json['data'] != null) {
      data = <CountryRes>[];
      json['data'].forEach((v) {
        data!.add(CountryRes.fromJson(v));
      });
    }
    totalCount = json['totalCount'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    if (this.data != null) {
      data['data'] = this.data!.map((v) => v.toJson()).toList();
    }
    data['totalCount'] = totalCount;
    return data;
  }
}

class CountryRes {
  String? sId;
  String? name;
  String? code;
  String? createdAt;
  String? updatedAt;
  String? countryFlag;
  int? iV;
  num? weeklyPrice;
  num? monthlyPrice;
  String get iCode {
    switch (code!.toLowerCase()) {
      case "zim":
        return "zw";
      case 'gh':
        return 'gh';
      case 'ngn':
        return 'ng';
      default:
        return code!.toLowerCase();
    }
  }

  //  String get iCode {
  //     switch (code!.toLowerCase()) {
  //       case "zim":
  //         return "zmw";
  //       case 'gh':
  //         return 'ghs';
  //       default:
  //         return "ngn";
  //     }
  //   }

  CountryRes(
      {this.sId,
      this.name,
      this.code,
      this.weeklyPrice,
      this.monthlyPrice,
      this.createdAt,
      this.updatedAt,
      this.iV,
      this.countryFlag});

  CountryRes.fromJson(Map<String, dynamic> json) {
    sId = json['_id'];
    name = json['name'];
    code = json['code'];
    countryFlag = json['country_flag'].toString();
    weeklyPrice = num.tryParse(json['weeklyPrice'].toString()) ?? 0;
    monthlyPrice = num.tryParse(json['monthlyPrice'].toString()) ?? 0;
    createdAt = json['createdAt'];
    updatedAt = json['updatedAt'];
    iV = json['__v'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['_id'] = sId;
    data['name'] = name;
    data['code'] = code;
    data['createdAt'] = createdAt;
    data['country_flag'] = countryFlag;
    data['updatedAt'] = updatedAt;
    data['weeklyPrice'] = weeklyPrice;
    data['monthlyPrice'] = monthlyPrice;
    data['__v'] = iV;
    return data;
  }

  @override
  String toString() {
    return 'CountryRes{sId: $sId, name: $name, code: $code, createdAt: $createdAt, updatedAt: $updatedAt, iV: $iV, iCode:$iCode, weekly: $weeklyPrice, monthly:$monthlyPrice, countryFlag:$countryFlag}';
  }
}

CountryRes allAfricanCountryRes = CountryRes(
    sId: '12',
    name: "All African",
    code: 'aff',
    countryFlag: 'ngn',
    createdAt: "",
    updatedAt: "",
    iV: 1,
    monthlyPrice: 0,
    weeklyPrice: 0);
