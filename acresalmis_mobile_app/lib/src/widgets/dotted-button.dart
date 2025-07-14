import 'package:flutter/material.dart';
import 'package:dotted_border/dotted_border.dart';

class DottedUploadContainer extends StatelessWidget {
  final VoidCallback? onTap;
  final String? title;

  const DottedUploadContainer({Key? key, this.title, this.onTap}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title?? "Term Of Reference",
          style: TextStyle(
            fontSize: 12,
            color: Colors.grey[800],
            fontWeight: FontWeight.w500,

          ),
        ),
        const SizedBox(height: 8), // Space between the title and the container
        GestureDetector(
          onTap: onTap,
          child: DottedBorder(
            borderType: BorderType.RRect,
            color: Colors.grey,
            dashPattern: [6, 3], // Dotted pattern: 6px line, 3px gap
            strokeWidth: 2,
            radius: const Radius.circular(12),
            child: Container(
              width: double.infinity,
              height: 80, // Adjust the height as needed
              color: Colors.white,
              child: Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Text(
                      "Click to upload",
                      style: TextStyle(
                        color: Colors.green,
                        fontSize: 16,
                        fontWeight: FontWeight.w600,
                        decoration: TextDecoration.underline,
                      ),
                    ),
                    const SizedBox(height: 4), // Space between texts
                    Text(
                      "JPG or PDF (max 2.3 MB)",
                      style: TextStyle(
                        color: Colors.grey[600],
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }
}
