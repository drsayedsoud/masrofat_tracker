package com.eltafouk.masareefi

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eltafouk.masareefi.ui.home.HomeScreen
import com.eltafouk.masareefi.ui.home.MasareefiViewModel
import com.eltafouk.masareefi.ui.theme.MasareefiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Keep screen always on (Always On Display)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()
        setContent {
            MasareefiTheme {
                val viewModel: MasareefiViewModel = viewModel()
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}
