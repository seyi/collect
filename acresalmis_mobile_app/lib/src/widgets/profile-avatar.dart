import 'package:flutter/material.dart';

class ProfileImageUploader extends StatelessWidget {
  final String imageUrl;
  final VoidCallback onCameraTap;
  final VoidCallback onUploadTap;

  const ProfileImageUploader({
    Key? key,
    required this.imageUrl,
    required this.onCameraTap,
    required this.onUploadTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Stack(
          children: [
            // Circular profile image
            CircleAvatar(
              radius: 50, // Adjust radius as needed
              backgroundImage: NetworkImage(imageUrl),
              backgroundColor: Colors.grey[200], // Fallback background color
            ),
            // Camera icon overlay
            Positioned(
              bottom: 5,
              right: 5,
              child: GestureDetector(
                onTap: onCameraTap,
                child: Container(
                  height: 30,
                  width: 30,
                  decoration: BoxDecoration(
                    color: Colors.green,
                    shape: BoxShape.circle,
                  ),
                  child: const Icon(
                    Icons.camera_alt,
                    color: Colors.white,
                    size: 18,
                  ),
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 8), // Spacing between image and text
        Text(
          "Upload Photo",
          style: TextStyle(
            color: Colors.grey[800],
            fontSize: 14,
            fontWeight: FontWeight.w500,
          ),
        ),
        const SizedBox(height: 4),
        GestureDetector(
          onTap: onUploadTap,
          child: Text(
            "Click to upload",
            style: TextStyle(
              color: Colors.green,
              fontSize: 14,
              decoration: TextDecoration.underline,
            ),
          ),
        ),
      ],
    );
  }
}
