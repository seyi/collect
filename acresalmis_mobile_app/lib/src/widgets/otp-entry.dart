import 'package:flutter/material.dart';
import 'package:flutter_template/constant/palette.dart';

class OtpEntryField extends StatefulWidget {
  final int length;
  final Function(String) onComplete;

  const OtpEntryField({
    Key? key,
    required this.length,
    required this.onComplete,
  }) : super(key: key);

  @override
  _OtpEntryFieldState createState() => _OtpEntryFieldState();
}

class _OtpEntryFieldState extends State<OtpEntryField> {
  final _textControllers = <TextEditingController>[];
  final _focusNodes = <FocusNode>[];

  @override
  void initState() {
    super.initState();
    for (var i = 0; i < widget.length; i++) {
      final controller = TextEditingController();
      controller.addListener(() {
        setState(() {}); // Trigger rebuild when the text changes
      });
      _textControllers.add(TextEditingController());
      _focusNodes.add(FocusNode());
    }
  }

  @override
  void dispose() {
    for (var controller in _textControllers) {
      controller.dispose();
    }
    for (var focusNode in _focusNodes) {
      focusNode.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: List.generate(widget.length, (index) {
        return Expanded(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 4.0),
            child: TextField(
              controller: _textControllers[index],
              focusNode: _focusNodes[index],
              textAlign: TextAlign.center,
              keyboardType: TextInputType.number,
              maxLength: 1,
              decoration: InputDecoration(
                filled: true,
                fillColor: _textControllers[index].text.isNotEmpty
                    ? primaryLight.withOpacity(0.2) // Green fill when text is entered
                    : Colors.transparent, // No fill when empty
                counterText: '',
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8.0),
                  borderSide: BorderSide(
                    color: _textControllers[index].text.isNotEmpty ? primaryLight : border,
                  ),
                ),
              ),
              onChanged: (value) {
                if (value.isNotEmpty && index < widget.length - 1) {
                  _focusNodes[index + 1].requestFocus();
                }
                if (value.isEmpty && index > 0) {
                  _focusNodes[index - 1].requestFocus();
                }
                if (index == widget.length - 1 && value.isNotEmpty) {
                  final otp = _textControllers.map((controller) => controller.text).join();
                  widget.onComplete(otp);
                }
              },
            ),
          ),
        );
      }),
    );
  }
}
