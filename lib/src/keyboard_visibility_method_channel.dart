import 'dart:async';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';

import 'keyboard_info.dart';
import 'keyboard_visibility_platform_interface.dart';

/// An implementation of [KeyboardVisibilityPlatform] that uses method channels.
@internal
class MethodChannelKeyboardVisibility extends KeyboardVisibilityPlatform {
  final eventChannel = const EventChannel('keyboard_visibility');

  static bool _isInitialized = false;

  static final _onChangeController = StreamController<KeyboardInfo>.broadcast();

  @override
  Stream<KeyboardInfo> get keyboardInfo {
    if (!_isInitialized) {
      eventChannel.receiveBroadcastStream().listen(
            (event) => updateData(event as Map),
          );
      _isInitialized = true;
    }
    return _onChangeController.stream;
  }

  void updateData(Map data) {
    final isVisible = data['isVisible'];
    final keyboardHeight = data['height'] as num;
    final duration = data['duration'];
    final info = KeyboardInfo(
      isVisible: isVisible,
      keyboardHeight: keyboardHeight.toDouble(),
      duration: duration,
    );
    _onChangeController.add(info);
  }
}
