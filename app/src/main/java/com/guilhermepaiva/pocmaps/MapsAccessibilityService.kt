package com.guilhermepaiva.pocmaps

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MapsAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MapsAccessibilityService"
    }

    private var delayMs: Long = 3000L // Default delay

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()
        delayMs = getSharedPreferences("com.guilhermepaiva.pocmaps", Context.MODE_PRIVATE)
            .getLong("delay_ms", 3000L)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            Log.d(TAG, "onAccessibilityEvent: $event")

            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
                event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                val rootNode = rootInActiveWindow
                rootNode?.let {
                    handler.postDelayed({
                        performStartButtonClick(it)
                        performContinueButtonClick(it)
                        performDoneButtonClick(it)
                    }, delayMs)
                }
            }
        }
    }

    private fun performStartButtonClick(rootNode: AccessibilityNodeInfo) {
        val startButton = findNodeByText(rootNode, "Start")
        startButton?.parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    private fun performContinueButtonClick(rootNode: AccessibilityNodeInfo) {
        val continueButton = findNodeByText(rootNode, "Continue")
        continueButton?.parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    private fun performDoneButtonClick(rootNode: AccessibilityNodeInfo) {
        val doneButton = findNodeByText(rootNode, "Done")
        doneButton?.parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        if (doneButton != null) {
            launchPOCMapsApp()
        }
    }

    private fun launchPOCMapsApp() {
        val launchIntent = packageManager.getLaunchIntentForPackage("com.guilhermepaiva.pocmaps")
        launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(launchIntent)
    }

    private fun performGoTabClick(rootNode: AccessibilityNodeInfo) {
        // Find the "Go" tab by its content description or resource ID
        val goTab = findNodeByText(rootNode, "Go")


        goTab?.apply {
            performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }

    private fun performSearch(rootNode: AccessibilityNodeInfo) {
        val searchBar = findNodeByContentDescription(rootNode, "Search here")

        searchBar?.apply {
            performAction(AccessibilityNodeInfo.ACTION_FOCUS)
            performAction(AccessibilityNodeInfo.ACTION_CLICK)
            performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, null)
        }
    }

    private fun findNodeByText(rootNode: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
        val nodeText = rootNode.text
        if (nodeText != null) {
            val nodeTextStr = when (nodeText) {
                is String -> nodeText
                is SpannableString -> nodeText.toString()
                else -> null
            }

            if (nodeTextStr != null && nodeTextStr == text) {
                return rootNode
            }
        }

        for (i in 0 until rootNode.childCount) {
            val childNode = rootNode.getChild(i)
            if (childNode != null) {
                val result = findNodeByText(childNode, text)
                if (result != null) {
                    return result
                }
            }
        }

        return null
    }

    private fun findNodeByContentDescription(rootNode: AccessibilityNodeInfo, description: String): AccessibilityNodeInfo? {
        rootNode.contentDescription?.let {
            if (it.toString() == description) {
                return rootNode
            }
        }

        for (i in 0 until rootNode.childCount) {
            val childNode = rootNode.getChild(i)
            val result = findNodeByContentDescription(childNode, description)
            if (result != null) {
                return result
            }
        }

        return null
    }

    override fun onInterrupt() {
        // Handle any cleanup if necessary
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            packageNames = arrayOf("com.google.android.apps.maps")
        }
        serviceInfo = info
    }

}