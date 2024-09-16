import Flutter
import UIKit

public class KeyboardVisibilityPlugin: NSObject, FlutterPlugin, FlutterStreamHandler {
    
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let eventChannel = FlutterEventChannel.init(name: "keyboard_visibility", binaryMessenger: registrar.messenger())
        let instance = KeyboardVisibilityPlugin()
        eventChannel.setStreamHandler(instance)
    }
    
    var eventSink:FlutterEventSink?
    
    var isVisible = false
    
    var isListening = false
    
    var lastNoti:NSNotification?
    
    
    public override init() {
        super.init()
        let notiCenter = NotificationCenter.default
        notiCenter.addObserver(self, selector: #selector(willShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        notiCenter.addObserver(self, selector: #selector(willHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    @objc func willShow(_ notification: NSNotification) {
        lastNoti = notification
        isVisible = true
        guard isListening else{
            return
        }
        let userInfo = notification.userInfo
        let kValue = (userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue
        let height = kValue?.height ?? 0.0
        let duration = (userInfo?[UIResponder.keyboardAnimationDurationUserInfoKey]) ?? 0
        eventSink?([
            "isVisible":true,
            "height": Double(height),
            "duration": Int.init(duration as! Double * 1000)
        ])
    }
    
   
    
    @objc func willHide(_ notification: NSNotification) {
        lastNoti = notification
        isVisible = false
        guard isListening else{
            return
        }
        let userInfo = notification.userInfo
        let duration = (userInfo?[UIResponder.keyboardAnimationDurationUserInfoKey]) ?? 0
        eventSink?([
            "isVisible":false,
            "height":0.0,
            "duration": Int.init(duration as! Double * 1000)
        ])
    }
    
    

    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        eventSink = events
        isListening = true
        if(isVisible){
            let userInfo = lastNoti?.userInfo
            let kValue = (userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue
            let height = kValue?.height ?? 0
            let duration = (userInfo?[UIResponder.keyboardAnimationDurationUserInfoKey]) ?? 0
            eventSink?([
                "isVisible":true,
                "height": height,
                "duration": duration
            ])
        }
        return nil
    }
    
    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
        isListening = false
        return nil
    }
}
