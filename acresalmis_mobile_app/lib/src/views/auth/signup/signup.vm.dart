import 'package:flutter_template/base.vm.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';

class SignUpViewModel extends BaseViewModel {
  String? email;
  String? fName;
  String? lName;
  String? phoneNumber;
  String? password;
  String? otp;

  void updateEmail(String v) => update(v, (v) => email = v);
  void updatePhone(String v) => update(v, (v) => phoneNumber = v);
  void updateFName(String v) => update(v, (v) => fName = v);
  void updateLName(String v) => update(v, (v) => lName = v);
  void updatePassword(String v) => update(v, (v) => password = v);
  void updateOtp(String v) => update(v, (v) => otp = v);

  bool get hasEmail => email.isNotNullNorEmpty;
  bool get hasPassword => email.isNotNullNorEmpty;
  bool get hasFName => email.isNotNullNorEmpty;
  bool get hasLName => email.isNotNullNorEmpty;
  bool get hasPhone => phoneNumber.isNotNullNorEmpty && phoneNumber!.length > 9;
  bool get validateField => hasEmail && hasFName && hasLName && hasPhone && hasPassword;

  registerUser() async {}
  verifyOtpUser() async {}
}
