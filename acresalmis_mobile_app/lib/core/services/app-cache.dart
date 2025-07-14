import 'package:flutter/material.dart';
import 'package:flutter_template/src/model/component-indicator-response.dart';
import 'package:flutter_template/src/model/pdo-response.dart';

class AppCache extends ChangeNotifier {
  List<PDOIndicatorModel> pdoIndicatorModel = [];

  List<ComponentIndicator>? componentIndicators = [];
}
