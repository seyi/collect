import 'package:flutter_dotenv/flutter_dotenv.dart';

/**
 * this class has all network configurations
 */
class NetworkConfig {
  static String BASE_URL = dotenv.env['BASE_URL']!;
  static String V24TEST = dotenv.env['V24TEST']!;
}
