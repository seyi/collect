enum AnalyticsEvents {
  otpVerification(title: "Otp Verification"),
  goPremium(title: "Go Premium"),
  referFriend(title: "Refer Friend"),
  planMeal(title: "Plan your meal for the next week"),
  logout(title: "Logout"),
  deleteAccount(title: "Delete Account"),
  editFoodPreferences(title: "Edit Food Preferences"),
  editFoodServices(title: "Edit Food Services"),
  reviewFoodServices(title: "Review Food Services"),
  termsOfUse(title: "Terms of Use"),
  changePassword(title: "Change Password"),
  privacyPolicy(title: "Privacy Policy"),
  contactUs(title: "Contact Us"),
  contactUsCall(title: "Contact us - call us"),
  contactUsWhatsapp(title: "Contact us via Whatsapp"),
  contactUsEmail(title: "Contact us via Email"),
  editProfile(title: "Edit Address Details"),
  createLineUp(title: "Create Meal Line Up"),
  selectMealPlan(title: "Select Meal Plan"),
  exploreMenu(title: "Explore Menu");

  final String? title;

  const AnalyticsEvents({this.title});
}
