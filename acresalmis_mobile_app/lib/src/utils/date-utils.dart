bool checkDateConditions(DateTime date) {
  DateTime now = DateTime.now();

  if (date.weekday == DateTime.sunday ||
      date.weekday == DateTime.saturday ||
      date.weekday == DateTime.monday) {
    return true;
  }
  if (date.isBefore(now)
      // ||
      // (now.day == date.day &&
      //     date.month == now.month &&
      //     now.year == date.year)
      ) {
    return false;
  } else if (date.weekday == DateTime.saturday) {
    if (now.weekday == DateTime.saturday && now.hour < 12) {
      return date.isAfter(now) &&
          (date.weekday == DateTime.saturday ||
              date.weekday == DateTime.sunday ||
              date.weekday == DateTime.monday);
    } else {
      return (date.weekday == DateTime.sunday ||
              date.weekday == DateTime.monday ||
              date.weekday == DateTime.tuesday) &&
          date.isAfter(now);
    }
  } else if (date.year == now.year &&
      date.month == now.month &&
      date.day == now.day) {
    return true;
  } else {
    if (date.year != now.year ||
        date.month != now.month ||
        date.day != now.day + 1) {
      return false; // Not the next day after the current date
    }

    if (now.hour >= 12) {
      return true; // Already past 2 PM
    }
    return false;
  }
}

DateTime getFutureDate() {
  DateTime currentDate = DateTime.now();
  bool conditionMet = false;

  // Check the condition for the current date before incrementing
  if (checkDatePredicate(currentDate)) {
    conditionMet = true;
  }

  // Continue checking with incremented dates until the condition is met
  while (!conditionMet) {
    currentDate = currentDate.add(const Duration(days: 1));
    conditionMet = checkDatePredicate(
        currentDate); // Replace with your actual condition check
  }

  return currentDate;
}

bool checkDatePredicate(DateTime dateTime) {
  return !checkDateConditions(dateTime);
}

DateTime getInitialDate() {
  DateTime now = DateTime.now();
  if (now.weekday == DateTime.saturday) {
    print('d is Sat');
    return DateTime.now().add(const Duration(days: 3));
  }
  if (now.weekday == DateTime.sunday) {
    print('d is Sun');
    return DateTime.now().add(const Duration(days: 2));
  }

  if (now.hour >= 12) {
    print('d is >12');
    if (now.weekday == DateTime.thursday || now.weekday == DateTime.friday) {
      print('d is Thurs || friday');
      return now.weekday == DateTime.thursday
          ? DateTime.now().add(const Duration(days: 5))
          : DateTime.now().add(const Duration(days: 5));
    } else {
      return DateTime.now().add(const Duration(days: 2));
    }
  } else {
    print('d is not greater than 12');
    return DateTime.now().add(const Duration(days: 1));
  }
}

DateTime getNextWeekdayDate(String weekday) {
  // Map of weekdays to their respective index values (Monday is 1, Sunday is 7)
  Map<String, int> weekdays = {
    'Monday': 1,
    'Tuesday': 2,
    'Wednesday': 3,
    'Thursday': 4,
    'Friday': 5,
    'Saturday': 6,
    'Sunday': 7,
  };

  // Get the current date
  DateTime now = DateTime.now();

  // Get the index of the current day
  int todayIndex = now.weekday;

  // Get the index of the target weekday
  int targetIndex = weekdays[weekday] ?? -1;

  if (targetIndex == -1) {
    throw ArgumentError('Invalid weekday: $weekday');
  }

  // Calculate the number of days to add to get to the target weekday
  int daysToAdd = (targetIndex - todayIndex + 7) % 7;

  // If the target day is today, or within the next two days, move to the next week
  if (daysToAdd == 0 || daysToAdd <= 2) {
    daysToAdd += 7;
  }

  // Get the next occurrence of the target weekday
  DateTime nextWeekdayDate = now.add(Duration(days: daysToAdd));

  return nextWeekdayDate;
}
