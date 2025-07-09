package com.example.home.content

import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun TaskTabs(
    selectedTabIndex: Int,
    onTabSelect: (Int) -> Unit
) {
    val tabs: List<String> = listOf("Все", "Незавершённые", "Завершённые")

    SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelect(index) },
                text = { Text(text = title, fontSize = 12.sp, maxLines = 1) }
            )
        }
    }
}