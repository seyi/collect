import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class LoadingWrapper extends StatelessWidget {
  final Widget child;
  final bool isLoading;
  final Function()? onDismiss;

  const LoadingWrapper({super.key, required this.child, required this.isLoading, this.onDismiss});

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        child,
        if (isLoading)
          Positioned.fill(
            child: GestureDetector(
              onTap: () => (onDismiss != null) ? onDismiss!() : null, // Dismiss when tapped
              behavior: HitTestBehavior.opaque,
              child: Container(
                color: Colors.black.withOpacity(0.5),
                child: Center(
                  child: Container(
                    padding: const EdgeInsets.all(16.0),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: const CupertinoActivityIndicator(
                      radius: 20,
                    ),
                  ),
                ),
              ),
            ),
          ),
      ],
    );
  }
}
