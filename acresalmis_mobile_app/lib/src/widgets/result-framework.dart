import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class ResultFrameworkComponent extends StatefulWidget {
  final String title;
  final String iconPath;
  final bool isSelected;
  final Function onSelected;

  const ResultFrameworkComponent({
    Key? key,
    required this.title,
    required this.iconPath,
    this.isSelected = false,
    required this.onSelected,
  }) : super(key: key);

  @override
  _ResultFrameworkComponentState createState() => _ResultFrameworkComponentState();
}

class _ResultFrameworkComponentState extends State<ResultFrameworkComponent> {
  bool isSelected = false;

  @override
  void initState() {
    super.initState();
    isSelected = widget.isSelected;
  }

  void toggleSelection() {
    setState(() {
      isSelected = !isSelected;
      widget.onSelected({
        'title': widget.title,
        'selected': isSelected,
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: toggleSelection,
      child: Container(
        margin: const EdgeInsets.symmetric(vertical: 8),
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: isSelected ? Colors.green : Colors.grey,
            width: 1.5,
          ),
          boxShadow: const [
            BoxShadow(
              color: Colors.black12,
              blurRadius: 8,
              offset: Offset(0, 2),
            ),
          ],
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            SvgPicture.asset(
              widget.iconPath,
              height: 32,
              width: 32,
              color: isSelected ? Colors.green : Colors.grey,
            ),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8.0),
                child: Text(
                  widget.title,
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 14,
                    color: isSelected ? Colors.green : Colors.black,
                  ),
                ),
              ),
            ),
            GestureDetector(
              onTap: toggleSelection,
              child: Icon(
                Icons.radio_button_checked,
                size: 20,
                color: isSelected ? Colors.green : Colors.grey,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
