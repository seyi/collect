class ResponseModel {
  final num? count;
  final String? message;
  final List<ComponentIndicator>? data;

  ResponseModel({
    this.count,
    this.message,
    this.data,
  });

  factory ResponseModel.fromJson(Map<String, dynamic> json) => ResponseModel(
        count: json['count'] as num?,
        message: json['message'] as String?,
        data: (json['data'] as List<dynamic>?)?.map((e) => ComponentIndicator.fromJson(e as Map<String, dynamic>)).toList(),
      );
}

class ComponentIndicator {
  final num? baseline;
  final String? componentOid;
  final String? componentDescription;
  final String? description;
  final String? frequency;
  final bool? isParent;
  final String? name;
  final String? parentId;
  final String? pbc;
  final String? sequence;
  final String? unitOfMeasure;
  final String? valueType;
  final List<dynamic>? subComponents;
  final List<dynamic>? targets;
  final String? oid;
  final String? createdBy;
  final DateTime? createdOn;
  final String? modifiedBy;
  final DateTime? modifiedOn;
  final String? deviceName;
  final String? deviceIpAddress;
  final String? deviceUserName;
  final num? latitude;
  final num? longitude;

  ComponentIndicator({
    this.baseline,
    this.componentOid,
    this.componentDescription,
    this.description,
    this.frequency,
    this.isParent,
    this.name,
    this.parentId,
    this.pbc,
    this.sequence,
    this.unitOfMeasure,
    this.valueType,
    this.subComponents,
    this.targets,
    this.oid,
    this.createdBy,
    this.createdOn,
    this.modifiedBy,
    this.modifiedOn,
    this.deviceName,
    this.deviceIpAddress,
    this.deviceUserName,
    this.latitude,
    this.longitude,
  });

  factory ComponentIndicator.fromJson(Map<String, dynamic> json) => ComponentIndicator(
        baseline: json['baseline'] as num?,
        componentOid: json['componentOid'] as String?,
        componentDescription: json['componentDescription'] as String?,
        description: json['description'] as String?,
        frequency: json['frequency'] as String?,
        isParent: json['isParent'] as bool?,
        name: json['name'] as String?,
        parentId: json['parentId'] as String?,
        pbc: json['pbc'] as String?,
        sequence: json['sequence'] as String?,
        unitOfMeasure: json['unitOfMeasure'] as String?,
        valueType: json['valueType'] as String?,
        subComponents: json['subComponents'] as List<dynamic>?,
        targets: json['targets'] as List<dynamic>?,
        oid: json['oid'] as String?,
        createdBy: json['createdBy'] as String?,
        createdOn: json['createdOn'] != null ? DateTime.tryParse(json['createdOn'] as String) : null,
        modifiedBy: json['modifiedBy'] as String?,
        modifiedOn: json['modifiedOn'] != null ? DateTime.tryParse(json['modifiedOn'] as String) : null,
        deviceName: json['deviceName'] as String?,
        deviceIpAddress: json['deviceIpAddress'] as String?,
        deviceUserName: json['deviceUserName'] as String?,
        latitude: json['latitude'] as num?,
        longitude: json['longitude'] as num?,
      );
}
