package com.example.taskman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskman.api.sync.SyncRepository
import com.example.taskman.ui.App
import com.example.taskman.ui.theme.TaskManTheme
import com.example.taskman.ui.utils.OptionViewModel
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val optionViewModel: OptionViewModel by inject<OptionViewModel>()
    private val syncRepository by inject<SyncRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        syncRepository
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