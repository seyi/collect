import 'package:flutter/material.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:flutter_template/src/widgets/number-pad.dart';
import 'package:flutter_template/src/widgets/pin-input.dart';

class PinScreen extends StatefulWidget {
  final Function(String) onDone;
  const PinScreen({super.key, required this.onDone});

  @override
  _PinScreenState createState() => _PinScreenState();
}

class _PinScreenState extends State<PinScreen> {
  List<String> pin = ["", "", "", ""]; // Holds the entered PIN

  void _updatePin(String value) {
    setState(() {
      for (int i = 0; i < pin.length; i++) {
        if (pin[i].isEmpty) {
          pin[i] = value;
          break;
        }
      }
    });
    widget.onDone(pin.join(""));
  }

  void _deletePin() {
    setState(() {
      for (int i = pin.length - 1; i >= 0; i--) {
        if (pin[i].isNotEmpty) {
          pin[i] = "";
          break;
        }
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.white,
      body: SafeArea(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Header Section
            Column(
              children: [
                Align(
                  alignment: Alignment.centerRight,
                  child: Padding(
                      padding: const EdgeInsets.only(right: 24),
                      child: GestureDetector(
                        onTap: () => Navigator.pop(context),
                        child: Icon(
                          Icons.close,
                          color: primaryDarkColor,
                        ),
                      )),
                ),
                Icon(Icons.shield, size: 64, color: primaryLight),
                const SizedBox(height: 20),
                Text(
                  "Enter your PIN",
                  style: TextStyle(color: primaryDarkColor, fontSize: 24, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 10),
                Text(
                  "To proceed please, enter your PIN",
                  textAlign: TextAlign.center,
                  style: TextStyle(color: primaryDarkColor, fontSize: 14),
                ),
              ],
            ),
            const SizedBox(height: 40),

            // PIN Input Section
            PinInputWidget(pin: pin),
            40.sbH,

            // Custom Number Pad
            CustomNumberPad(
              onNumberPressed: _updatePin,
              onDeletePressed: _deletePin,
            ),
          ],
        ),
      ),
    );
  }
}

showTransactionPinBottomSheet(BuildContext context, Function(String) onDone) {
  showModalBottomSheet(
      enableDrag: true,
      isDismissible: true,
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.white,
      builder: (context) {
        return PinScreen(
          onDone: (v) => onDone(v),
        );
      });
}
