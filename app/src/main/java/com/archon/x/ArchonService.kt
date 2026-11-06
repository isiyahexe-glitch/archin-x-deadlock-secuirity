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
        var instance: ArchonService? = null

        // මේ Function එකෙන් තමයි App එකේ Button එකෙන් වැඩේ නවත්තන්නේ
        fun stopAttack() {
            triggerReport = false
            instance?.isRunning = false
            instance?.handler?.removeCallbacksAndMessages(null) // දැනට ඉන්න delay එකත් කැන්සල් කරනවා
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!triggerReport || isRunning) return
        val rootNode = rootInActiveWindow ?: return
        isRunning = true
        executeReportSequence(rootNode)
    }

    private fun executeReportSequence(root: AccessibilityNodeInfo) {
        if (!triggerReport) return // Stop කරලා නම් වැඩේ කරන්නේ නෑ
        val menuBtn = findNodeByDesc(root, "More options") ?: findNodeById(root, "com.whatsapp:id/menuitem_conversation_menu")
        if (menuBtn != null) {
            menuBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            handler.postDelayed({ clickReportOption() }, Random.nextLong(800, 1500))
        } else {
            reset()
        }
    }

    private fun clickReportOption() {
        if (!triggerReport) return
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
        if (!triggerReport) return
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
        if (triggerReport) { // අලුතෙන් loop එක පටන් ගන්නේ triggerReport true නම් විතරයි
            handler.postDelayed({ 
                val root = rootInActiveWindow
                if (root != null && triggerReport) {
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
        stopAttack()
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }
}
