import 'package:flutter/cupertino.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class StorageService {
  FlutterSecureStorage storage = const FlutterSecureStorage(
      aOptions: AndroidOptions(
    encryptedSharedPreferences: true,
  ));
  bool isLoggedIn = false;

  storeItem({String? key, String? value}) async {
    await storage.write(key: key!, value: value);
  }

  Future<dynamic> readItem({String? key}) async {
    try {
      final value = await storage.read(key: key!);
      return value;
    } catch (e) {
      debugPrint(e.toString());
    }
  }

  void deleteItem({String? key}) async {
    await storage.delete(key: key!);
  }

  void deleteAllItems() async {
    await storage.deleteAll();
  }
}
