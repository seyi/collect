class Address {
  String? address;
  String? city;
  String? country;
  String? postalCode;

//<editor-fold desc="Data Methods">
  Address({
    this.address,
    this.city,
    this.country,
    this.postalCode,
  });

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is Address &&
          runtimeType == other.runtimeType &&
          address == other.address &&
          city == other.city &&
          country == other.country &&
          postalCode == other.postalCode);

  @override
  int get hashCode =>
      address.hashCode ^ city.hashCode ^ country.hashCode ^ postalCode.hashCode;

  @override
  String toString() {
    return 'Address{ address: $address, city: $city, country: $country, postalCode: $postalCode,}';
  }

  Address copyWith({
    String? address,
    String? city,
    String? country,
    String? postalCode,
  }) {
    return Address(
      address: address ?? this.address,
      city: city ?? this.city,
      country: country ?? this.country,
      postalCode: postalCode ?? this.postalCode,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'address_': address,
      'city': city,
      'country': country,
      'postalCode': postalCode,
    };
  }

  Map<String, dynamic> toPartyRequestMap() {
    return {
      'address_': address,
      'city': city,
      'country': country,
      'postcode': postalCode,
    };
  }

  factory Address.fromMap(Map<String, dynamic> map) {
    return Address(
      address: map['address'] as String,
      city: map['city'] as String,
      country: map['country'] as String,
      postalCode: map['postalCode'] as String,
    );
  }
  factory Address.fromPartyMealsMap(Map<String, dynamic> map) {
    return Address(
      address: map['address_'] as String,
      city: map['city'] as String,
      country: map['country'] as String,
      postalCode: map['postcode'] as String,
    );
  }

//</editor-fold>
}
