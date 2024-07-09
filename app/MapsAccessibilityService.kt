
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MapsAccessibilityService: AccessibilityService() {
    companion object {
        private const val TAG = "MapsAccessibilityService"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            Log.d(TAG, "onAccessibilityEvent: $event")

            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                val rootNode = rootInActiveWindow
                rootNode?.let {
                    performSearch(it)
                }
            }
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
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            packageNames = arrayOf("com.google.android.apps.maps")
        }
        serviceInfo = info
    }

}