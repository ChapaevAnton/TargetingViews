package com.dpt.targetingviews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dpt.inclineviews.InclinePlatformView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<InclinePlatformView>(R.id.incline_platform1)

        findViewById<InclinePlatformView>(R.id.incline_platform2)
            .setAngle(-45.0)
        findViewById<InclinePlatformView>(R.id.incline_platform3)
            .isInverted(true)
            .setAngle(-35.0)
        findViewById<InclinePlatformView>(R.id.incline_platform4)
            .setAngle(-25.0)
    }
}