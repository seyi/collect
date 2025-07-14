import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_template/src/model/component-model.dart';

class SubComponent extends StatefulWidget {
  final String title;
  final String iconPath;
  final bool hasChildren;
  final List<String>? children;
  final bool isSelected;
  // final ValueChanged<Map<String, dynamic>> onSelected;
  final Function onSelected;
  final SubComponentEnum subComponentEnum;

  const SubComponent(
      {Key? key, required this.title, required this.iconPath, this.hasChildren = false, this.children, this.isSelected = false, required this.onSelected, required this.subComponentEnum})
      : super(key: key);

  @override
  _SubComponentState createState() => _SubComponentState();
}

class _SubComponentState extends State<SubComponent> {
  bool isExpanded = false;
  bool isSelected = false;
  String? selectedChild;

  @override
  void initState() {
    super.initState();
    isSelected = widget.isSelected;
  }

  void toggleSelection() {
    setState(() {
      isSelected = !isSelected;
      widget.onSelected({
        'type': 'parent',
        'title': widget.title,
        'selected': isSelected,
      });
    });
  }

  void toggleChildSelection(String child) {
    setState(() {
      selectedChild = selectedChild == child ? null : child;
      widget.onSelected({
        'type': 'child',
        'parentTitle': widget.title,
        'childTitle': selectedChild,
      });
    });
  }

  void toggleDropdown() {
    setState(() {
      isExpanded = !isExpanded;
    });
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: widget.hasChildren ? toggleDropdown : toggleSelection,
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
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
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
                        fontSize: 11,
                        color: isSelected ? Colors.green : Colors.black,
                      ),
                    ),
                  ),
                ),
                widget.hasChildren
                    ? GestureDetector(
                        onTap: toggleDropdown,
                        child: Icon(
                          isExpanded ? Icons.arrow_drop_down : Icons.arrow_forward_ios,
                          size: 18,
                          color: isSelected ? Colors.green : Colors.black,
                        ),
                      )
                    : GestureDetector(
                        onTap: toggleSelection,
                        child: Icon(
                          Icons.radio_button_checked,
                          size: 20,
                          color: isSelected ? Colors.green : Colors.grey,
                        ),
                      ),
              ],
            ),
            if (isExpanded && widget.subComponentEnum.semiSubComponents != null) ...[
              const SizedBox(height: 8),
              Divider(color: Colors.grey[300]),
              const SizedBox(height: 8),
              ...widget.subComponentEnum.semiSubComponents!.map(
                (child) => Padding(
                  padding: const EdgeInsets.symmetric(vertical: 10),
                  child: Row(
                    children: [
                      Icon(
                        Icons.circle,
                        size: 8,
                        color: selectedChild == child ? Colors.green : Colors.black,
                      ),
                      const SizedBox(width: 15),
                      Expanded(
                        child: GestureDetector(
                          onTap: () => toggleChildSelection(child.label!),
                          child: Text(
                            child.label ?? "",
                            style: TextStyle(
                              fontSize: 14,
                              color: selectedChild == child ? Colors.green : Colors.black,
                            ),
                          ),
                        ),
                      ),
                      GestureDetector(
                        onTap: () => toggleChildSelection(child.label!),
                        child: Icon(
                          Icons.radio_button_checked,
                          size: 20,
                          color: selectedChild == child ? Colors.green : Colors.grey,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

class ISubComponent extends StatefulWidget {
  final String title;
  final String iconPath;
  final bool hasChildren;
  final List<String>? children;
  final bool isSelectedSubcomponent;
  final SemiSubComponentEnum? selectedSemiSubcomponent;

  final Function(SubComponentEnum, SemiSubComponentEnum?) onSelected;
  final SubComponentEnum subComponentEnum;

  const ISubComponent(
      {Key? key,
      required this.title,
      required this.iconPath,
      this.hasChildren = false,
      this.children,
      this.isSelectedSubcomponent = false,
      required this.onSelected,
      required this.subComponentEnum,
      this.selectedSemiSubcomponent})
      : super(key: key);

  @override
  _ISubComponentState createState() => _ISubComponentState();
}

class _ISubComponentState extends State<ISubComponent> {
  // bool isExpanded = false;
  bool isSelected = false;
  String? selectedChild;

  ValueNotifier<bool> isExpanded = ValueNotifier(false);
  ValueNotifier<SemiSubComponentEnum?> semiSC = ValueNotifier(null);
  bool get hasChildren => widget.subComponentEnum.semiSubComponents != null;
  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder(
      valueListenable: isExpanded,
      builder: (context, expanded, _) => GestureDetector(
        onTap: () {
          if (hasChildren) {
            isExpanded.value = !isExpanded.value;
          } else {
            widget.onSelected(widget.subComponentEnum, null);
          }
        },
        child: Container(
          margin: const EdgeInsets.symmetric(vertical: 8),
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(12),
            border: Border.all(
              color: widget.isSelectedSubcomponent ? Colors.green : Colors.grey,
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
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  SvgPicture.asset(
                    widget.iconPath,
                    height: 32,
                    width: 32,
                    color: widget.isSelectedSubcomponent ? Colors.green : Colors.grey,
                  ),
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 8.0),
                      child: Text(
                        widget.title,
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 11,
                          color: isSelected ? Colors.green : Colors.black,
                        ),
                      ),
                    ),
                  ),
                  hasChildren
                      ? GestureDetector(
                          onTap: () => isExpanded.value = !isExpanded.value,
                          child: Icon(
                            expanded ? Icons.arrow_drop_down : Icons.arrow_forward_ios,
                            size: 18,
                            color: widget.isSelectedSubcomponent ? Colors.green : Colors.black,
                          ),
                        )
                      : GestureDetector(
                          onTap: () => widget.onSelected(widget.subComponentEnum, null),
                          child: Icon(
                            Icons.radio_button_checked,
                            size: 20,
                            color: widget.isSelectedSubcomponent ? Colors.green : Colors.grey,
                          ),
                        ),
                ],
              ),
              if (expanded && hasChildren) ...[
                const SizedBox(height: 8),
                Divider(color: Colors.grey[300]),
                const SizedBox(height: 8),
                ...widget.subComponentEnum.semiSubComponents!.map(
                  (child) => Padding(
                    padding: const EdgeInsets.symmetric(vertical: 10),
                    child: GestureDetector(
                      behavior: HitTestBehavior.opaque,
                      onTap: () {
                        widget.onSelected(widget.subComponentEnum, child);
                      },
                      child: Row(
                        children: [
                          Icon(
                            Icons.circle,
                            size: 8,
                            color: widget.selectedSemiSubcomponent == child ? Colors.green : Colors.black,
                          ),
                          const SizedBox(width: 15),
                          Expanded(
                            child: Text(
                              child.label ?? "",
                              style: TextStyle(
                                fontSize: 14,
                                color: widget.selectedSemiSubcomponent == child ? Colors.green : Colors.black,
                              ),
                            ),
                          ),
                          Icon(
                            Icons.radio_button_checked,
                            size: 20,
                            color: widget.selectedSemiSubcomponent == child ? Colors.green : Colors.grey,
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}
