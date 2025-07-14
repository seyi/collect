enum ElectricityServiceTypeEnum {
  prepaid(title: "Prepaid",code: 'prepaid'),
  postpaid(title: "Postpaid", code: 'postpaid');

  final String? title;
  final String? code;
  const ElectricityServiceTypeEnum({this.title, this.code});
}
