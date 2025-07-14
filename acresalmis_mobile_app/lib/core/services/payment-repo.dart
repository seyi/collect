import 'package:flutter/cupertino.dart';
import 'package:flutter_template/locator.dart';

final paymentRepo = getIt<PaymentRepository>();

class PaymentRepository extends ChangeNotifier {
  void reset({bool country = true, continent = true}) {}
}
