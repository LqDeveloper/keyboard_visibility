import 'package:flutter/material.dart';

import 'keyboard_info.dart';
import 'keyboard_tool.dart';

typedef KeyboardVisibleCallback = Widget Function(
  BuildContext context,
  KeyboardInfo info,
);

class KeyboardVisibleWidget extends StatefulWidget {
  final KeyboardVisibleCallback builder;

  const KeyboardVisibleWidget({super.key, required this.builder});

  @override
  State<KeyboardVisibleWidget> createState() => _KeyboardVisibleWidgetState();
}

class _KeyboardVisibleWidgetState extends State<KeyboardVisibleWidget> {

  @override
  Widget build(BuildContext context) {
    return StreamBuilder(
        stream: KeyboardTool.keyboardInfo,
        builder: (cxt, snapData) {
          return widget.builder(
            cxt,
            snapData.data ??
                const KeyboardInfo(
                  isVisible: false,
                  keyboardHeight: 0,
                  duration: 0,
                ),
          );
        });
  }
}
