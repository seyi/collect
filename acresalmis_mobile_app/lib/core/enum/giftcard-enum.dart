enum GiftCardType {
  physical(title: "Physical", value: "PHYSICAL"),
  ecode(title: "Ecode", value: "E-CODE"),
  none(title: "none", value: "");

  final String? title;
  final String? value;
  const GiftCardType({this.title, this.value});
}
