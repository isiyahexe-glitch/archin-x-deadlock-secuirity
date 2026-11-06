package com.archon.x

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.os.Handler
import android.os.Looper
import kotlin.random.Random

class ArchonService : AccessibilityService() {

    companion object {
        var triggerReport = false
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!triggerReport || isRunning) return
        val rootNode = rootInActiveWindow ?: return
        isRunning = true
        executeReportSequence(rootNode)
    }

    private fun executeReportSequence(root: AccessibilityNodeInfo) {
        val menuBtn = findNodeByDesc(root, "More options") ?: findNodeById(root, "com.whatsapp:id/menuitem_conversation_menu")
        if (menuBtn != null) {
            menuBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            handler.postDelayed({ clickReportOption() }, Random.nextLong(800, 1500))
        } else {
            reset()
        }
    }

    private fun clickReportOption() {
        val root = rootInActiveWindow ?: return reset()
        val reportBtn = findNodeByText(root, "Report") ?: findNodeByText(root, "Report business account")
        if (reportBtn != null) {
            reportBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            handler.postDelayed({ confirmReport() }, Random.nextLong(1000, 2000))
        } else {
            performGlobalAction(GLOBAL_ACTION_BACK)
            reset()
        }
    }

    private fun confirmReport() {
        val root = rootInActiveWindow ?: return reset()
        val confirmBtn = findNodeByText(root, "Report") 
        if (confirmBtn != null) {
            confirmBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            handler.postDelayed({ 
                performGlobalAction(GLOBAL_ACTION_BACK)
                handler.postDelayed({ reset() }, Random.nextLong(1500, 2500))
            }, Random.nextLong(1000, 1500))
        } else {
            performGlobalAction(GLOBAL_ACTION_BACK)
            reset()
        }
    }

    private fun reset() {
        isRunning = false
        if (triggerReport) {
            handler.postDelayed({ 
                val root = rootInActiveWindow
                if (root != null) {
                    isRunning = true
                    executeReportSequence(root)
                } else {
                    triggerReport = false 
                }
            }, Random.nextLong(2000, 4000))
        }
    }

    private fun findNodeByDesc(root: AccessibilityNodeInfo, desc: String): AccessibilityNodeInfo? {
        val list = root.findAccessibilityNodeInfosByViewId(desc)
        if (!list.isNullOrEmpty()) return list[0]
        return null
    }

    private fun findNodeById(root: AccessibilityNodeInfo, id: String): AccessibilityNodeInfo? {
        val list = root.findAccessibilityNodeInfosByViewId(id)
        if (!list.isNullOrEmpty()) return list[0]
        return null
    }

    private fun findNodeByText(root: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
        val list = root.findAccessibilityNodeInfosByText(text)
        for (node in list) {
            if (node.text?.toString()?.equals(text, ignoreCase = true) == true) {
                return node
            }
        }
        return null
    }

    override fun onInterrupt() {
        triggerReport = false
        isRunning = false
    }
}
