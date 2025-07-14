import 'package:flutter_template/src/utils/string-extensions.dart';

class LoginRequest {
  final String? email;
  String? password;
  String? userName;
  String? securityPin;
  LoginRequest({this.email, this.password, this.userName});

  LoginRequest.withPin({this.email, this.securityPin});

  Map<String, dynamic> toMap() {
    return {'email': email, if (password.isNotNullNorEmpty) 'password': password, if (securityPin.isNotNullNorEmpty) "security_pin": securityPin};
  }

  Map<String, dynamic> toAcreasalLogin() {
    return {
      'userName': userName,
      'password': password,
    };
  }

  @override
  String toString() {
    return 'LoginRequest{email: $email, password: $password, userName: $userName, securityPin: $securityPin}';
  }
}

class SignUpRequest {
  final String? email;
  final String? password;
  final String? name;
  final String? phone;
  final String? passwordConfirmation;
  SignUpRequest({
    this.email,
    this.password,
    this.phone,
    this.passwordConfirmation,
    this.name,
  });

  Map<String, dynamic> toMap() {
    return {
      'email': email,
      'password': password,
      'name': name,
      'phone': phone,
      'password_confirmation': passwordConfirmation,
    };
  }

  @override
  String toString() {
    return 'SignUpRequest{email: $email, password: $password, name: $name, phone: $phone, passwordConfirmation: $passwordConfirmation}';
  }
}

class VerifyEmailRequest {
  final String? email;
  String? verifyCode;

  VerifyEmailRequest({this.email, this.verifyCode});

  Map<String, dynamic> toMap() {
    return {
      'email': email,
      'verify_code': verifyCode,
    };
  }

  @override
  String toString() {
    return 'VerifyEmailRequest{email: $email, verifyCode: $verifyCode}';
  }
}
