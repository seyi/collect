enum MobileOp {
  mtn(title: "MTN", icon: "mtn", code: 'mtn'),
  airtel(title: "Airtel", icon: 'airtel', code: 'airtel'),
  etisalat(title: "9Mobile", icon: 'etisalat', code: '9mobile'),
  glo(title: "Glo", icon: 'glo', code: 'glo');

  final String? title;
  final String? icon;
  final String? code;
  const MobileOp({this.title, this.icon, this.code});
}

enum CableOp {
  dstv(title: "DSTV", icon: "dstv", code: 'dstv'),
  startimes(title: "startimes", icon: 'airtel', code: 'startimes'),
  gotv(title: "GOTV", icon: 'gotv', code: 'gotv'),
  showmax(title: "ShowMax", icon: 'showmax', code: 'showmax');

  final String? title;
  final String? icon;
  final String? code;
  const CableOp({this.title, this.icon, this.code});
}
