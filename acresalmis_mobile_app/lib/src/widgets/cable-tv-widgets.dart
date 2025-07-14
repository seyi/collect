import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_template/core/enum/mobile-operator-eunm.dart';
import 'package:flutter_template/src/utils/string-extensions.dart';

class CableWidgetItem extends StatelessWidget {
  final CableOp mobileOp;
  final Function(CableOp)? onTap;
  final bool isChecked;

  const CableWidgetItem({
    Key? key,
    required this.mobileOp,
    this.onTap,
    required this.isChecked,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => onTap!(mobileOp),
      child: Stack(
        children: [
          Container(
            padding: const EdgeInsets.all(16.0),
            decoration: BoxDecoration(
              color: Colors.white,
              border: Border.all(color: const Color(0xffE0F6FF), width: 2.0),
              borderRadius: BorderRadius.circular(8.0),
              boxShadow: [
                BoxShadow(
                  color: Colors.grey.shade200,
                  blurRadius: 2.0,
                  offset: const Offset(0, 2),
                ),
              ],
            ),
            child: Image.asset(
              mobileOp.icon!.png,
              width: 64.0,
              height: 64.0,
            ),
          ),
          if (isChecked) Positioned(right: 8, top: 5, child: SvgPicture.asset('selected-tick'.svg))
        ],
      ),
    );
  }
}

class CableWidgetGrid extends StatelessWidget {
  final Function(CableOp) onTapCallback;

  CableWidgetGrid({
    Key? key,
    required this.onTapCallback,
  }) : super(key: key);
  final ValueNotifier<CableOp?> network = ValueNotifier(null);
  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 90,
      child: GridView.builder(
        padding: const EdgeInsets.all(16.0),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 4,
          mainAxisSpacing: 16.0,
          crossAxisSpacing: 16.0,
        ),
        itemCount: CableOp.values.length,
        itemBuilder: (context, index) {
          return ValueListenableBuilder(
              valueListenable: network,
              builder: (context, service, _) {
                bool isSelected = service == CableOp.values[index];
                return CableWidgetItem(
                    mobileOp: CableOp.values[index],
                    isChecked: isSelected,
                    onTap: (v) {
                      network.value = v;
                      onTapCallback(v);
                    });
              });
        },
      ),
    );
  }
}

// Example usage
