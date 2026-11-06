package com.archon.x

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.BLACK)
            setPadding(50, 100, 50, 50)
        }

        val title = TextView(this).apply {
            text = "ARCHON_X"
            setTextColor(android.graphics.Color.RED)
            textSize = 50f
            gravity = android.view.Gravity.CENTER
            setPadding(0, 50, 0, 20)
        }

        val status = TextView(this).apply {
            text = "FIND. TEST. SECURE."
            setTextColor(android.graphics.Color.GRAY)
            textSize = 16f
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 100)
        }

        val btnEnable = Button(this).apply {
            text = "ENABLE MODULE"
            setBackgroundColor(android.graphics.Color.DKGRAY)
            setTextColor(android.graphics.Color.WHITE)
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        val btnStart = Button(this).apply {
            text = "EXECUTE MASS REPORT"
            setBackgroundColor(android.graphics.Color.RED)
            setTextColor(android.graphics.Color.WHITE)
            setOnClickListener {
                ArchonService.triggerReport = true
            }
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(btnEnable)
        layout.addView(btnStart)
        
        setContentView(layout)
    }
}
