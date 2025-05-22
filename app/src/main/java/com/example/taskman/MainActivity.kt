package com.example.taskman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskman.api.sync.SyncRepository
import com.example.taskman.ui.App
import com.example.taskman.ui.theme.TaskManTheme
import com.example.taskman.ui.utils.ThemeRepository
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val syncRepository by inject<SyncRepository>()
    private val themeRepository by inject<ThemeRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        syncRepository
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by themeRepository.getThemeFlow().collectAsStateWithLifecycle(
                isSystemInDarkTheme()
            )

            TaskManTheme(darkTheme = isDarkTheme) {
                App(
                    isDarkTheme = isDarkTheme,
                    toggleTheme = { themeRepository.saveTheme(!isDarkTheme) }
                )
            }
        }
    }
}