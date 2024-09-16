
class KeyboardInfo {
  final bool isVisible;
  final double keyboardHeight;
  final int duration;

  const KeyboardInfo({
    required this.isVisible,
    required this.keyboardHeight,
    required this.duration,
  });

  @override
  String toString() {
    return '''
    isVisible:$isVisible
    keyboardHeight: $keyboardHeight
    duration(ms):$duration
    ''';
  }
}
