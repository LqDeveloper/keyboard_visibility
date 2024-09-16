import 'package:keyboard_visibility/keyboard_visibility.dart';
import 'keyboard_info.dart';
import 'keyboard_visibility_platform_interface.dart';

class KeyboardTool {
  KeyboardTool._();

  static Stream<KeyboardInfo> get keyboardInfo =>
      KeyboardVisibilityPlatform.instance.keyboardInfo;
}
