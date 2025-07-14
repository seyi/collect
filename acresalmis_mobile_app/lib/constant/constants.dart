import 'package:flutter/services.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

const String appName = 'AppName';
const String bearerToken = 'token';
const String bearerTokenType = 'tokenType';
const String userToken = 'userToken';
const String introScreen = 'introScreen';
const String currentUser = 'currentUser';
const String appleAuthObject = 'appleAuthObject';
const String settings = 'settings';
const String imagePath = "assets/images/";
const String removeTokenEndPoint = 'token/remove';
const String naira = '₦';
const String misAppLink = "https://ee.kobotoolbox.org/x/nMQP7hiB";
String get privacyPolicy => dotenv.env['PRIVACY_POLICY']!;
String get appEnv => dotenv.env['ENV']!;

String get termsOfService => dotenv.env['TERMS_OF_SERVICE']!;
String chatUrl = dotenv.env['CHATLINK']!;
String get encryptionKey => dotenv.env['ENCRYPTION_KEY']!;
//    width: 50.w,

List<TextInputFormatter> numberFilter = [FilteringTextInputFormatter.digitsOnly];
