import 'package:flutter/foundation.dart';
import 'package:flutter_template/core/services/analytics-services.dart';
import 'package:flutter_template/core/services/app-cache.dart';
import 'package:flutter_template/core/services/app-settings.dart';
import 'package:flutter_template/core/services/navigation_service.dart';
import 'package:flutter_template/core/services/payment-repo.dart';
import 'package:flutter_template/core/services/storage-service.dart';
import 'package:flutter_template/core/services/user.service.dart';
import 'package:flutter_template/core/services/web-services/app-api-service.dart';
import 'package:flutter_template/core/services/web-services/auth.api.dart';
import 'package:flutter_template/core/services/web-services/components.api.dart';
import 'package:flutter_template/core/services/web-services/transactions-api-service.dart';
import 'package:flutter_template/core/services/web-services/user-api-service.dart';
import 'package:flutter_template/src/views/auth/forgot-password/forgot-password.vm.dart';
import 'package:flutter_template/src/views/auth/login/login.vm.dart';
import 'package:flutter_template/src/views/auth/signup/signup.vm.dart';
import 'package:flutter_template/src/views/components/component.vm.dart';
import 'package:flutter_template/src/views/home/home.vm.dart';
import 'package:flutter_template/src/views/onboarding/onboarding.vm.dart';
import 'package:get_it/get_it.dart';

GetIt getIt = GetIt.I;

dependenciesInjectorSetup() {
  //View Model
  try {
    // Services
    getIt.registerFactory<OnBoardingViewModel>(() => OnBoardingViewModel());
    getIt.registerFactory<SignUpViewModel>(() => SignUpViewModel());
    getIt.registerFactory<LoginViewModel>(() => LoginViewModel());
    getIt.registerFactory<HomeViewModel>(() => HomeViewModel());
    getIt.registerFactory<ComponentViewModel>(() => ComponentViewModel());
    getIt.registerFactory<ForgotPasswordViewModel>(() => ForgotPasswordViewModel());
    getIt.registerLazySingleton<NavigationService>(() => NavigationService());
    // getIt.registerLazySingleton(() => Initializer());
    getIt.registerLazySingleton(() => UserService());
    getIt.registerLazySingleton(() => AppCache());

    // getIt.registerLazySingleton(() => FirebaseService());
    getIt.registerLazySingleton(() => PaymentRepository());
    getIt.registerLazySingleton(() => AnalyticsService());
    getIt.registerLazySingleton<StorageService>(() => StorageService());
    getIt.registerLazySingleton<AuthenticationApiService>(() => AuthenticationApiService());
    getIt.registerLazySingleton<UserApiService>(() => UserApiService());
    getIt.registerLazySingleton<TransactionsApiService>(() => TransactionsApiService());
    getIt.registerLazySingleton<AppApiService>(() => AppApiService());
    getIt.registerLazySingleton<ComponentsApiService>(() => ComponentsApiService());

    getIt.registerLazySingleton<AppSettingsService>(() => AppSettingsService());

//  ChangePasswordViewModel
  } catch (e) {
    debugPrint("dependency Injector Setup");
    debugPrint(e.toString());
  }
}
