package com.example.taskman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskman.ui.App
import com.example.taskman.ui.theme.TaskManTheme
import com.example.taskman.ui.utils.OptionViewModel
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val optionViewModel: OptionViewModel by inject<OptionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by optionViewModel.isDarkTheme.collectAsStateWithLifecycle()

            TaskManTheme(darkTheme = isDarkTheme) {
                App(
                    isDarkTheme = isDarkTheme,
                    toggleTheme = { optionViewModel.toggleTheme() }
                )
            }
        }
    }
}