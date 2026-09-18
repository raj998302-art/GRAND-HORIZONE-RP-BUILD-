package com.grandhorizonrp.launcher.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grandhorizonrp.launcher.R

/**
 * About screen — Grand Horizon RP branding, project description, and the
 * third-party notices/credits. LICENSE/NOTICE files found in the supplied
 * package are listed here so attribution is preserved (NOT stripped).
 */
class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val tv = findViewById<TextView>(R.id.tvCredits)
        tv.movementMethod = LinkMovementMethod.getInstance()
    }
}
