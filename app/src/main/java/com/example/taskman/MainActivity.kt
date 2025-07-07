package com.example.taskman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.repository.ThemeRepository
import com.example.sync.SyncManager
import com.example.theme.TaskManTheme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var syncManager: SyncManager
    @Inject
    lateinit var themeRepository: ThemeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        syncManager

        enableEdgeToEdge()

        setContent {
            val isDarkTheme by themeRepository.isDarkTheme().collectAsStateWithLifecycle(
                initialValue = false
            )

            TaskManTheme(darkTheme = isDarkTheme) {
                App(
                    isDarkTheme = isDarkTheme,
                    toggleTheme = {
                        lifecycleScope.launch {
                            themeRepository.saveTheme(!isDarkTheme)
                        }
                    }
                )
            }
        }
    }
}