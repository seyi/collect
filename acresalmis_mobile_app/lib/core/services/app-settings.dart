import 'package:flutter/material.dart';
import 'package:flutter_template/core/services/storage-service.dart';
import 'package:flutter_template/locator.dart';

class AppSettingsService {
  AppSettingsService() {
    readAppSettings();
  }

  DateTime? lastConnectTime;
  DateTime? lastDisconnectionTime;

  final storage = getIt<StorageService>();

  readAppSettings() async {
    debugPrint("readAppSettings");
    String? s1 = await storage.readItem(key: "");
    String? s2 = await storage.readItem(key: "");

    print("readAppSettings::${s1}");
    print("readAppSettings::${s2}");

    (s1 == null || s1 == "false") ? settingsOption1 = ValueNotifier(false) : settingsOption1 = ValueNotifier(true);

    (s2 == null || s2 == "false") ? settingsOption2 = ValueNotifier(false) : settingsOption2 = ValueNotifier(true);
  }

  toggleSingleMealsAU(bool show) async {
    storage.storeItem(key: '', value: show.toString());
    settingsOption1.value = show;
  }

  toggleBulkMealsAU(bool show) async {
    storage.storeItem(key: '', value: show.toString());
    settingsOption2.value = show;
  }

  toggleSavingsWalletVisibility(bool show) async {
    storage.storeItem(key: "", value: show.toString());
    settingsOption3.value = show;
  }

  late ValueNotifier<bool> settingsOption1;
  late ValueNotifier<bool> settingsOption2;
  late ValueNotifier<bool> settingsOption3;
}
