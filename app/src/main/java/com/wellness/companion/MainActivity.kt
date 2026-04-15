package com.wellness.companion

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.wellness.companion.ui.WellnessAppRoot

/**
 * FragmentActivity is required by androidx.biometric, which attaches its
 * prompt fragment to the host. We still get all modern Compose / activity
 * features because FragmentActivity extends ComponentActivity.
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as WellnessApp).container
        setContent {
            WellnessAppRoot(container = container)
        }
    }
}
