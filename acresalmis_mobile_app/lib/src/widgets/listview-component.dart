import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class CustomListCard extends StatelessWidget {
  final String title;
  final String subtitle;
  final String code;
  final String imagePath;
  final Function() onEditPressed;

  const CustomListCard({
    Key? key,
    required this.title,
    required this.subtitle,
    required this.code,
    required this.imagePath,
    required this.onEditPressed,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 4, // Slight elevation to give depth
      shadowColor: Colors.black.withOpacity(0.2), // Lighter shadow
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10), // Rounded corners
      ),
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 5.0),
        child: ListTile(
          contentPadding: const EdgeInsets.symmetric(horizontal: 16.0),
          leading: SvgPicture.asset(
            imagePath, // Path to the SVG image
            height: 40,
            width: 40,
          ),
          title: Text(
            title,
            style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold),
          ),
          subtitle: Text(
            subtitle,
            style: const TextStyle(fontSize: 10, color: Colors.grey),
          ),
          trailing: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(
                    "Code",
                    style: TextStyle(fontSize: 10, color: Colors.grey),
                  ),
                  Text(
                    code,
                    style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600),
                  ),
                ],
              ),
              const SizedBox(width: 12),
              IconButton(
                onPressed: onEditPressed,
                icon: const Icon(Icons.edit, color: Colors.green),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
