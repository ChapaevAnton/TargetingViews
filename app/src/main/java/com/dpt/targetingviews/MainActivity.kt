package com.dpt.targetingviews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dpt.inclineviews.InclinePlatformView
import com.dpt.inclineviews.TargetingView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<InclinePlatformView>(R.id.incline_platform1)

        val targetView = findViewById<TargetingView>(R.id.targeting_view1).apply {

        }

        targetView.setOnClickListener {
            targetView.setPoint(
                title = "test",
                depth = 1.0,
                angle = 2.0,
                mX = 0.5,
                mY = 0.5,
                distance = 5.0,
                heading = 0.0,
                azimuth = null
            )
        }
    }
}