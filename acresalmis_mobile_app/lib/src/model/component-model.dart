enum ComponentEnum {
  componentA(name: "Component A", label: "Dry Land Management ", icon: 'assets/images/ca.svg', code: 0, subComponentEnum: [
    SubComponentEnum.subComponentA1,
    SubComponentEnum.subComponentA2,
    SubComponentEnum.subComponentA3,
  ]),
  componentB(name: "Component B", icon: 'assets/images/cb.svg', label: "Community Climate Resilience", code: 1, subComponentEnum: [
    SubComponentEnum.subComponentB1,
    SubComponentEnum.subComponentB2,
  ]),
  componentC(name: "Component C", icon: 'assets/images/cc.svg', label: "Institutional Strengthening and Project Management", code: 3, subComponentEnum: [
    SubComponentEnum.subComponentC1,
    SubComponentEnum.subComponentC2,
  ]),
  componentD(name: "Component D", icon: 'assets/images/cd.svg', label: "Resettlement Action Plan Implementation", code: 4, subComponentEnum: []);

  final String? name;
  final num? code;
  final String? label;
  final String? icon;
  final List<SubComponentEnum>? subComponentEnum;
  const ComponentEnum({this.name, this.label, this.code, this.subComponentEnum, this.icon});
}

enum SubComponentEnum {
  subComponentA1(name: "Subcomponent A1", label: "Strategic Watershed Planning", code: 5, parentComponent: 1),
  subComponentA2(name: "Subcomponent A2", label: "Landscape Investments", code: 6, parentComponent: 1, semiSubComponents: [
    SemiSubComponentEnum.a2Afforestation,
    SemiSubComponentEnum.a2CivilWorks,
    SemiSubComponentEnum.a2WaterResMgt,
  ]),
  subComponentA3(name: "Subcomponent A3", label: "Special Ecosystems", code: 7, parentComponent: 1),
  subComponentB1(
      name: "Subcomponent B1",
      label: "Community Strengthening",
      code: 8,
      parentComponent: 2,
      semiSubComponents: [SemiSubComponentEnum.b1CommunityStrengthening, SemiSubComponentEnum.b1CapacityBuilding, SemiSubComponentEnum.b1MicroShed, SemiSubComponentEnum.b1PeaceBuilding]),
  subComponentB2(
      name: "Subcomponent B2",
      label: "Community Investments",
      code: 9,
      parentComponent: 2,
      semiSubComponents: [SemiSubComponentEnum.b2CommunityInv, SemiSubComponentEnum.b2FarmerLedIrrigationDev, SemiSubComponentEnum.b2InputDistribution, SemiSubComponentEnum.b2LandscapeRestoration]),
  subComponentC1(name: "Subcomponent C1", label: "Institutional and Policy Strengthening", code: 10, parentComponent: 3),
  subComponentC2(name: "Subcomponent C2", label: "Project Management", code: 11, parentComponent: 3, semiSubComponents: [
    SemiSubComponentEnum.c2StateStaging,
    SemiSubComponentEnum.c2Procurement,
    SemiSubComponentEnum.c2FinancialMgt,
    SemiSubComponentEnum.c2EnvInstruments,
    SemiSubComponentEnum.c2MonitoringAndEvaluation
  ]);

  final String? name;
  final num? code;
  final String? label;
  final num? parentComponent;
  final List<SemiSubComponentEnum>? semiSubComponents;
  const SubComponentEnum({this.name, this.code, this.label, this.parentComponent, this.semiSubComponents});
}

enum SemiSubComponentEnum {
  a2Afforestation(name: "Subcomponent A2", label: "Afforestation", parentComponent: 1, subComponent: 6),
  a2CivilWorks(name: "Subcomponent A2", label: "Civil Works", parentComponent: 1, subComponent: 6),
  a2WaterResMgt(name: "Subcomponent A2", label: "Water Resources Management", parentComponent: 1, subComponent: 6),
  b1CommunityStrengthening(name: "Subcomponent B1", label: "Community Strengthening", parentComponent: 2, subComponent: 8),
  b1CapacityBuilding(name: "Subcomponent B1", label: "Capacity Building", parentComponent: 2, subComponent: 8),
  b1MicroShed(name: "Subcomponent B1", label: "Micro Watershed Plans", parentComponent: 2, subComponent: 8),
  b1PeaceBuilding(name: "Subcomponent B1", label: "Peace Building & Social Cohesion", parentComponent: 2, subComponent: 8),
  b2CommunityInv(name: "Subcomponent B2", label: "Community Investments", parentComponent: 2, subComponent: 9),
  b2FarmerLedIrrigationDev(name: "Subcomponent B2", label: "Farmer-Led Irrigation Development", parentComponent: 2, subComponent: 9),
  b2InputDistribution(name: "Subcomponent B2", label: "Inputs/Assets Distribution", parentComponent: 2, subComponent: 9),
  b2LandscapeRestoration(name: "Subcomponent B2", label: "Landscape Restoration", parentComponent: 2, subComponent: 9),
  c2StateStaging(name: "Subcomponent C2", label: "State Staging", parentComponent: 3, subComponent: 11),
  c2Procurement(name: "Subcomponent C2", label: "Procurements", parentComponent: 3, subComponent: 11),
  c2FinancialMgt(name: "Subcomponent C2", label: "Financial Management", parentComponent: 3, subComponent: 11),
  c2EnvInstruments(name: "Subcomponent C2", label: "Environmental & Social Safeguards Instruments", parentComponent: 3, subComponent: 11),
  c2MonitoringAndEvaluation(name: "Subcomponent C2", label: "Monitoring and Evaluation", parentComponent: 3, subComponent: 11);

  final String? name;
  final String? label;
  final num? subComponent;
  final num? parentComponent;
  const SemiSubComponentEnum({this.name, this.label, this.parentComponent, this.subComponent});
}

extension SubComp on SubComponentEnum {
  ComponentEnum get getParentComponent {
    switch (this.parentComponent) {
      case 1:
        return ComponentEnum.componentA;
      case 2:
        return ComponentEnum.componentA;
      case 3:
        return ComponentEnum.componentA;
      default:
        return ComponentEnum.componentA;
    }
  }
}

extension SemiSubComp on SemiSubComponentEnum {
  SubComponentEnum get getSubComponent {
    switch (subComponent) {
      case 5:
        return SubComponentEnum.subComponentA1;
      case 6:
        return SubComponentEnum.subComponentA2;
      case 8:
        return SubComponentEnum.subComponentB1;
      case 9:
        return SubComponentEnum.subComponentB2;
      case 11:
        return SubComponentEnum.subComponentC2;
      default:
        return SubComponentEnum.subComponentA1;
    }
  }
}

class ComponentModel {
  final ComponentEnum componentEnum;
  final SubComponentEnum? subComponentEnum;
  final SemiSubComponentEnum? semiSubComponentEnum;
  ComponentModel({required this.componentEnum, this.subComponentEnum, this.semiSubComponentEnum});
}
