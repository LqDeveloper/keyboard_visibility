import 'package:meta/meta.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'keyboard_info.dart';
import 'keyboard_visibility_method_channel.dart';

@internal
abstract class KeyboardVisibilityPlatform extends PlatformInterface {
  /// Constructs a KeyboardVisibilityPlatform.
  KeyboardVisibilityPlatform() : super(token: _token);

  static final Object _token = Object();

  static KeyboardVisibilityPlatform _instance =
      MethodChannelKeyboardVisibility();

  /// The default instance of [KeyboardVisibilityPlatform] to use.
  ///
  /// Defaults to [MethodChannelKeyboardVisibility].
  static KeyboardVisibilityPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [KeyboardVisibilityPlatform] when
  /// they register themselves.
  static set instance(KeyboardVisibilityPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Stream<KeyboardInfo> get keyboardInfo {
    throw UnimplementedError("子类需要实现这个");
  }
}
