import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_template/constant/palette.dart';

class Input extends StatefulWidget {
  final bool? readOnly;
  final bool? enabled;
  final bool? obscureText;
  final bool? autocorrect;
  final bool? enableSuggestions;
  final TextEditingController? controller;
  final Widget? suffixIcon;
  final String? labelText;
  final String? hintText;
  final Function()? onTap;
  final Function(String?)? onSaved;
  final TextInputType? keyboardType;
  final TextInputAction? textInputAction;
  final String? helperText;
  final int? maxLines;
  final Function(String)? onChanged;
  final Function(String)? onSubmit;
  final Widget? suffix;
  final bool? autofocus;
  final String? prefixText;
  final Widget? prefixWidget;
  final List<TextInputFormatter>? inputFormatters;
  final double borderRadius;
  final Widget? prefixIcon;
  final List<String>? autofillHints;
  final int? maxLength;

  const Input(
      {Key? key,
      this.labelText,
      this.hintText,
      this.onChanged,
      this.helperText,
      this.controller,
      this.autofillHints,
      this.onSubmit,
      this.suffixIcon,
      this.keyboardType,
      this.prefixWidget,
      this.onTap,
      this.onSaved,
      this.autofocus,
      this.maxLines,
      this.textInputAction,
      this.borderRadius = 8,
      this.autocorrect = false,
      this.enableSuggestions = true,
      this.obscureText = false,
      this.suffix,
      this.prefixText,
      this.readOnly = false,
      this.enabled = true,
      this.inputFormatters,
      this.prefixIcon,
      this.maxLength})
      : super(key: key);

  @override
  State<Input> createState() => _InputState();
}

class _InputState extends State<Input> {
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
    super.dispose();
    _focus.removeListener(_onFocusChange);
    _focus.dispose();
  }

  void _onFocusChange() {
    setState(() {
      _isFocus = _focus.hasFocus;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          padding: const EdgeInsets.symmetric(
            horizontal: 12.0,
            vertical: 1.0,
          ),
          decoration: BoxDecoration(
            color: _isFocus
                ? inputFocus
                : white, // (!widget.enabled! == true ? const Color(0xFFEFF2EE) : white),
            border: Border.all(
              width: 2,
              color: _isFocus ? primaryColor : textFieldBorder,
            ),
            borderRadius: BorderRadius.circular(widget.borderRadius),
          ),
          child: TextFormField(
            maxLength: widget.maxLength,
            autofillHints: widget.autofillHints,
            onFieldSubmitted: widget.onSubmit,
            style: TextStyle(color: primaryDarkColor),
            enabled: widget.enabled,
            readOnly: widget.readOnly!,
            inputFormatters: widget.inputFormatters ?? [],
            autofocus: widget.autofocus ?? false,
            focusNode: _focus,
            onChanged: widget.onChanged,
            maxLines: widget.maxLines ?? 1,
            controller: widget.controller,
            autocorrect: widget.autocorrect ?? true,
            enableSuggestions: widget.enableSuggestions ?? true,
            obscureText: widget.obscureText ?? false,
            keyboardType: widget.keyboardType,
            textInputAction: widget.textInputAction,
            onTap: widget.onTap,
            onSaved: widget.onSaved,
            decoration: InputDecoration(
              counterText: "",
              // border: OutlineInputBorder(borderSide: BorderSide.none),
              contentPadding: const EdgeInsets.fromLTRB(0, 4, 0, 4),
              hintStyle: const TextStyle(height: 0, fontSize: 15, color: Color(0xff98A2B3)),
              labelStyle: TextStyle(height: 1, fontSize: 15, color: primaryDarkColor),
              border: InputBorder.none,
              labelText: widget.labelText,
              hintText: widget.hintText,
              suffixIcon: widget.suffixIcon,
              // prefixText: widget.prefixText,
              prefix: widget.prefixText != null
                  ? Text(
                      widget.prefixText!,
                      style: const TextStyle(fontFamily: "inter"),
                    )
                  : null,
              prefixIcon: widget.prefixWidget,
              // suffixIconConstraints: BoxConstraints.tight(Size(30.w, 40.h)),
              suffix: widget.suffix,
            ),
          ),
        ),
        if (widget.helperText != null)
          Padding(
            padding: const EdgeInsets.only(top: 4.0),
            child: Text(
              widget.helperText!,
              style: TextStyle(fontSize: 12, color: textDark),
            ),
          )
      ],
    );
  }
}
