enum BillType {
  componentA(title: "Component A", showSearch: false, icon: 'betting', route: '/bettingRoute'),
  componentB(title: "Component B", showSearch: false, icon: 'other-bills-s', route: '/otherBillsRoute'),
  componentC(title: "Component C", showSearch: false, icon: 'tv-s', route: '/cableTvRoute'),
  componentD(title: "Component D", showSearch: false, icon: 'tv-s', route: '/cableTvRoute');

  final String? title;
  final bool showSearch;
  final String? icon;
  final String? route;
  const BillType({this.title, this.showSearch = false, this.icon, this.route});
}



enum ComponentType {
  componentA(
    title: "Component A",
    showBorder: true,
    icon: 'ca',
    route: '/bottom_nav_page',
    description: "Dry-Land Management.",
  ),
  componentB(
    title: "Component B",
    showBorder: false,
    icon: 'ca',
    route: '/bottom_nav_page',
    description: "Community Climate Resilience.",
  ),
  componentC(
    title: "Component C",
    showBorder: false,
    icon: 'ca',
    route: '/bottom_nav_page',
    description: "Institutional Strengthening and Project Management.",
  ),
  componentD(
    title: "Component D",
    showBorder: false,
    icon: 'ca',
    route: '/bottom_nav_page',
    description: "Contingent Emergency Response.",
  );

  final String? title;
  final bool showBorder;
  final String? icon;
  final String? route;
  final String? description;

  const ComponentType({
    this.title,
    this.showBorder = false,
    this.icon,
    this.route,
    this.description,
  });
}
