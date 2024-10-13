package com.example.keyboard_visibility

import android.app.Activity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel


/** KeyboardVisibilityPlugin */
class KeyboardVisibilityPlugin : FlutterPlugin, EventChannel.StreamHandler, ActivityAware,
    KeyboardHeightListener {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var eventChannel: EventChannel
    private var eventSink: EventChannel.EventSink? = null
    private var keyboardUtils: KeyboardUtils = KeyboardUtils()

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "keyboard_visibility")
        eventChannel.setStreamHandler(this)
    }


    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        eventChannel.setStreamHandler(null)
        unregisterListener()
    }


    ///EventChannel.StreamHandler
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
    }

    override fun onCancel(arguments: Any?) {
        eventSink = null
    }

    private fun listenForKeyboard(activity: Activity) {
        keyboardUtils.registerListener(activity, this)
    }

    private fun unregisterListener() {
        keyboardUtils.unregisterLister()
    }

    ///ActivityAware
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        listenForKeyboard(binding.activity)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        unregisterListener()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        listenForKeyboard(binding.activity)
    }

    override fun onDetachedFromActivity() {
        unregisterListener()
    }

    override fun onKeyboardChanged(isVisible: Boolean, height: Double, duration: Int) {
        eventSink?.success(
            mapOf(
                "isVisible" to isVisible,
                "height" to height,
                "duration" to duration
            )
        )
    }
}
