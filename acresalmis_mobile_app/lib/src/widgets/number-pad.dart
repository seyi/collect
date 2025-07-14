import 'package:flutter/material.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';

class CustomNumberPad extends StatelessWidget {
  final Function(String) onNumberPressed;
  final VoidCallback onDeletePressed;

  const CustomNumberPad({
    super.key,
    required this.onNumberPressed,
    required this.onDeletePressed,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ...[
          ['1', '2', '3'],
          ['4', '5', '6'],
          ['7', '8', '9'],
          [
            '0',
          ]
        ].map((row) {
          return Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: row.map((number) {
              return number == "0"
                  ? Row(
                      children: [
                        Visibility(
                          visible: true,
                          child: IconButton(
                            icon: Icon(Icons.backspace, color: AppColors.white),
                            onPressed: () {},
                          ),
                        ),
                        22.sbW,
                        _NumberButton(
                          number: number,
                          onPressed: () => onNumberPressed(number),
                        ),
                        22.sbW,
                        IconButton(
                          icon: Icon(Icons.backspace, color: Colors.grey.shade300),
                          onPressed: onDeletePressed,
                        ),
                      ],
                    )
                  : _NumberButton(
                      number: number,
                      onPressed: () => onNumberPressed(number),
                    );
            }).toList(),
          );
        }),
      ],
    );
  }
}

class _NumberButton extends StatelessWidget {
  final String number;
  final VoidCallback onPressed;

  const _NumberButton({
    Key? key,
    required this.number,
    required this.onPressed,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onPressed,
      child: Container(
        margin: const EdgeInsets.all(12),
        height: 60,
        width: 60,
        decoration: BoxDecoration(
          color: Colors.grey.shade800.withOpacity(.4),
          shape: BoxShape.circle,
        ),
        child: Center(
          child: Text(
            number,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 24,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
      ),
    );
  }
}
