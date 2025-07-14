import 'package:flutter/material.dart';
import 'package:flutter_template/constant/palette.dart';

class PinInputWidget extends StatelessWidget {
  final List<String> pin;

  const PinInputWidget({Key? key, required this.pin}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: List.generate(
        pin.length,
        (index) => Container(
          margin: const EdgeInsets.symmetric(horizontal: 8),
          height: 50,
          width: 50,
          decoration: BoxDecoration(
            border: Border.all(
              color: pin[index].isNotEmpty ? primaryLight : Colors.grey,
              width: 2,
            ),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Center(
            child: Text(
              pin[index],
              style: TextStyle(
                color: primaryDarkColor,
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ),
      ),
    );
  }
}
