import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_template/src/widgets/input.dart';
import 'package:sliding_up_panel/sliding_up_panel.dart';

class ListBottomSheet<T> extends StatefulWidget {
  final String? title;
  final List<T> list;
  final Function(T) onSelected;
  final Widget Function(T item) itemBuilder;
  final bool showSearch;

  const ListBottomSheet({
    super.key,
    required this.list,
    required this.onSelected,
    required this.itemBuilder,
    this.title,
    this.showSearch = true,
  });

  @override
  _ListBottomSheetState<T> createState() => _ListBottomSheetState<T>();
}

class _ListBottomSheetState<T> extends State<ListBottomSheet<T>> {
  late List<T> list_;
  late List<T> queryResult;

  @override
  void initState() {
    super.initState();
    _initializeList();
  }

  void _initializeList() {
    list_ = widget.list;
    queryResult = widget.list;
  }

  void _searchQuery(String query) {
    setState(() {
      if (query.isNotEmpty) {
        queryResult = list_.where((item) => item.toString().toLowerCase().contains(query.toLowerCase())).toList();
      } else {
        queryResult = List.from(list_);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return SlidingUpPanel(
      borderRadius: BorderRadius.circular(20),
      maxHeight: MediaQuery.of(context).size.height - 40.h,
      minHeight: MediaQuery.of(context).size.height / 1.5.h,
      panelBuilder: (ScrollController sc) => ClipRRect(
        borderRadius: const BorderRadius.only(topLeft: Radius.circular(20), topRight: Radius.circular(20)),
        child: Container(
          // color: context.colorScheme.surfaceContainer,
          padding: EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 20.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                if (widget.title != null)
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20.0),
                    child: Text(
                      widget.title!,
                      style: TextStyle(
                        color: const Color(0xFF0B0806),
                        fontSize: 18.r,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                if (widget.title != null) SizedBox(height: 20.h),

                if (widget.showSearch)
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20.0),
                    child: TextField(
                      onChanged: _searchQuery,
                      decoration: InputDecoration(
                        prefixIcon: const Icon(Icons.search),
                        labelText: "Search",
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(8),
                        ),
                      ),
                    ),
                  ),
                if (widget.showSearch) SizedBox(height: 20.h),

                // List View
                Expanded(
                  child: SingleChildScrollView(
                    controller: sc,
                    child: ListView.builder(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: queryResult.length,
                      itemBuilder: (BuildContext context, int index) {
                        final item = queryResult[index];
                        return GestureDetector(
                          onTap: () {
                            Navigator.pop(context);
                            widget.onSelected(item);
                          },
                          child: widget.itemBuilder(item),
                        );
                      },
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

showListBottomSheet<T>(
  BuildContext context, {
  required List<T> list,
  required Function(T) onSelected,
  required Widget Function(T item) itemBuilder,
  String? title,
  bool showSearch = true,
}) {
  showModalBottomSheet(
    enableDrag: true,
    isDismissible: true,
    context: context,
    isScrollControlled: true,
    backgroundColor: Colors.transparent,
    builder: (context) {
      return ListBottomSheet<T>(
        list: list,
        onSelected: onSelected,
        itemBuilder: itemBuilder,
        title: title,
        showSearch: showSearch,
      );
    },
  );
}

class DropdownInput<T> extends StatelessWidget {
  final String labelText;
  final String title;
  final List<T> items;
  final Widget Function(T) itemBuilder;
  final Function(T) onSelected;
  final bool showSearch;

  const DropdownInput({
    Key? key,
    required this.labelText,
    required this.title,
    required this.items,
    required this.itemBuilder,
    required this.onSelected,
    this.showSearch = false,
  }) : super(key: key);

  void _showBottomSheet(BuildContext context) {
    showListBottomSheet<T>(
      context,
      onSelected: onSelected,
      list: items,
      itemBuilder: itemBuilder,
      showSearch: showSearch,
      title: title,
    );
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => _showBottomSheet(context),
      child: Input(
        labelText: labelText,
        suffixIcon: const Icon(Icons.keyboard_arrow_down_sharp),
        enabled: false,
        readOnly: true,
      ),
    );
  }
}
