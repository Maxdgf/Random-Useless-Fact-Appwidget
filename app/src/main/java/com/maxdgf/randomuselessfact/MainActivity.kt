package com.maxdgf.randomuselessfact

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.maxdgf.randomuselessfact.ui.theme.RandomUselessFactTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RandomUselessFactTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Text guide, how to pin widget
                    val appName = stringResource(R.string.app_name)
                    Text(
                        text = """
                               To pin $appName's appwidget:
                            
                            1) Long-press on the home screen, 
                            2) Tap the "Widgets" button, 
                            3) Then find and add the "Mem Info" widget.
                            4) Done!
                        """.trimIndent()
                    )
                }
            }
        }
    }
}