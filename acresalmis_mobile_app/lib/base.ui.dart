import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:flutter_template/constant/palette.dart';
import 'package:flutter_template/locator.dart';
import 'package:flutter_template/src/utils/widget_extensions.dart';
import 'package:provider/provider.dart';

import 'base.vm.dart';

class BaseView<T extends BaseViewModel> extends StatefulWidget {
  final Widget Function(BuildContext context, T model, Widget? child)? builder;
  final Function(T)? onModelReady;
  const BaseView({Key? key, this.builder, this.onModelReady}) : super(key: key);

  @override
  _BaseViewState<T> createState() => _BaseViewState<T>();
}

class _BaseViewState<T extends BaseViewModel> extends State<BaseView<T>> {
  T model = getIt<T>();

  @override
  void initState() {
    super.initState();
    if (widget.onModelReady != null) {
      widget.onModelReady!(model);
    }
  }

  // @override
  // void dispose() {
  //   super.dispose();
  // }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider<T>(
      create: (_) => model,
      child: Consumer<T>(
        builder: (_, model, __) => Stack(
          children: [
            widget.builder!.call(_, model, __),
            if (model.isLoading)
              Stack(children: [
                SizedBox(
                  height: height(context),
                  child: ModalBarrier(
                    color: Colors.white.withOpacity(.7),
                    dismissible: false,
                  ),
                ),
                Center(
                    child: SpinKitChasingDots(
                  color: primaryColor,
                ))
              ])
            else
              const SizedBox(),
            if (model.hasError)
              Scaffold(
                backgroundColor: Colors.black12.withOpacity(.9),
                body: Stack(children: [
                  ModalBarrier(
                    // color: Colors.black12.withOpacity(.9),
                    dismissible: false,
                  ),
                  Center(
                      child: Column(
                    children: [
                      const Spacer(),
                      Align(
                        alignment: Alignment.centerRight,
                        child: Padding(
                            padding: const EdgeInsets.only(right: 24),
                            child: GestureDetector(
                              onTap: () => model.clearError(),
                              child: const Icon(
                                Icons.close,
                                color: AppColors.white,
                              ),
                            )),
                      ),
                      50.sbH,
                      Padding(
                        padding: EdgeInsets.symmetric(horizontal: 12.w),
                        child: Text(
                          model.errorMessage!,
                          style: TextStyle(color: Colors.white, fontSize: 18.sp),
                          textAlign: TextAlign.center,
                        ),
                      ),
                      if (model.retryFunction != null) GestureDetector(onTap: () => model.retryFunction!(), child: const Icon(Icons.refresh)),
                      const Spacer(),
                    ],
                  ))
                ]),
              )
            else
              const SizedBox(),
          ],
        ),
      ),
    );
  }
}
