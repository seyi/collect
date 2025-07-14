import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class DateInput extends StatefulWidget {
  final bool? readOnly;
  final bool? enabled;
  final TextEditingController? controller;
  final String? labelText;
  final String? hintText;
  final double borderRadius;

  const DateInput({
    Key? key,
    this.labelText,
    this.hintText,
    this.controller,
    this.readOnly = false,
    this.enabled = true,
    this.borderRadius = 8,
  }) : super(key: key);

  @override
  State<DateInput> createState() => _DateInputState();
}

class _DateInputState extends State<DateInput> {
  late FocusNode _focus;
  bool _isFocus = false;

  @override
  void initState() {
    super.initState();
    _focus = FocusNode();
    _focus.addListener(_onFocusChange);
  }

  @override
  void dispose() {
    _focus.removeListener(_onFocusChange);
    _focus.dispose();
    super.dispose();
  }

  void _onFocusChange() {
    setState(() {
      _isFocus = _focus.hasFocus;
    });
  }

  Future<void> _selectDate(BuildContext context) async {
    DateTime? pickedDate = await showDatePicker(
      context: context,
      initialDate: DateTime.now(),
      firstDate: DateTime(2000),
      lastDate: DateTime(2101),
    );
    if (pickedDate != null) {
      widget.controller?.text = "${pickedDate.toLocal()}".split(' ')[0];
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12.0, vertical: 0.0),
      decoration: BoxDecoration(
        color: _isFocus ? Colors.grey[200] : Colors.white,
        border: Border.all(
          width: 1,
          color: _isFocus ? Colors.blue : Colors.grey.withOpacity(0.5),
        ),
        borderRadius: BorderRadius.circular(widget.borderRadius),
      ),
      child: TextFormField(
        controller: widget.controller,
        readOnly: true, // Make the field read-only
        focusNode: _focus,
        onTap: () => _selectDate(context), // Trigger the date picker
        decoration: InputDecoration(
          isDense: true, // Ensures a compact layout
          contentPadding: const EdgeInsets.symmetric(
            vertical: 4.0, // Reduced padding inside the text field
            horizontal: 0.0, // Minimal horizontal padding
          ),
          border: InputBorder.none,
          labelText: widget.labelText,
          hintText: widget.hintText,
        ),
      ),
    );
  }
}
