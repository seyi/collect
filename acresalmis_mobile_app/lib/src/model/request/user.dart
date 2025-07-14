class UpdateUserRequest {
  final String? gender;
  final String? avatar;
  final Address? address;
  final String? firstName;
  final String? lastName;
  final String? email;
  final String? phone;

  UpdateUserRequest(
      {this.avatar,
      this.gender,
      this.address,
      this.phone,
      this.firstName,
      this.lastName,
      this.email});

  @override
  String toString() {
    return 'UpdateUserRequest{gender: $gender, avatar: $avatar, address: ${address.toString()}}';
  }

  Map<String, dynamic> toMap() {
    Map<String, dynamic> map = {
      'first_name': firstName,
      'last_name': lastName,
      'phone': phone,
      'email': email,
      'gender': gender,
      'avatar': avatar,
      'address': address?.toMap(),
    };
    map.removeWhere((key, value) => value == null);
    return map;
  }

  factory UpdateUserRequest.fromMap(Map<String, dynamic> map) {
    return UpdateUserRequest(
      gender: map['gender'] as String,
      avatar: map['avatar'] as String,
      address: map['address'] as Address,
    );
  }
}

class Address {
  String? address_;
  String? city;
  String? postcode;
  String? country;
  Address({this.country, this.address_, this.city, this.postcode});

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Address &&
          runtimeType == other.runtimeType &&
          address_ == other.address_ &&
          city == other.city &&
          postcode == other.postcode &&
          country == other.country;

  @override
  int get hashCode =>
      address_.hashCode ^ city.hashCode ^ postcode.hashCode ^ country.hashCode;

  Map<String, dynamic> toMap() {
    Map<String, dynamic> map = {
      'address_': address_,
      'city': city,
      'postcode': postcode,
      'country': country,
    };
    map.removeWhere((key, value) => value == null);
    return map;
  }

  factory Address.fromMap(Map<String, dynamic> map) {
    return Address(
      address_: map['address_'] as String,
      city: map['city'] as String,
      postcode: map['postcode'] as String,
      country: map['country'] as String,
    );
  }
}
