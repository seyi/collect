import 'package:flutter/material.dart';

class DummyBtn extends StatelessWidget {
  final Function() onClick;
  const DummyBtn({super.key, required this.onClick});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(onTap: () => onClick(), child: const Icon(Icons.add));
  }
}
