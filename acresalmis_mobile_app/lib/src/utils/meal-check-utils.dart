void performAction(
    {required bool a, // A is boolean
    required String? b, // B is a nullable string
    required bool c, // C is boolean
    required String? d, // D is a nullable string
    required Function() action}) {
  bool canPerformAction =
      false; // Flag to determine if the action can be performed

  // Check if both A and C are true
  if (a && c) {
    // Both A and C are true, check if B and D are not null or empty
    if (b != null && b.isNotEmpty && d != null && d.isNotEmpty) {
      canPerformAction = true; // Action can be performed
    } else {
      print('Both B and D must be valid when A and C are true.');
    }
  } else if (a) {
    // Only A is true, check if B is not null or empty
    if (b != null && b.isNotEmpty) {
      canPerformAction = true; // Action can be performed
    } else {
      print('B is null or empty when A is true.');
    }
  } else if (c) {
    // Only C is true, check if D is not null or empty
    if (d != null && d.isNotEmpty) {
      canPerformAction = true; // Action can be performed
    } else {
      print('D is null or empty when C is true.');
    }
  } else {
    // Both A and C are false
    print('No action is performed since both A and C are false.');
  }

  // Perform action if criteria are met
  if (canPerformAction) {
    print('Action is carried out with B = $b, D = $d');
    action();
  }
}
