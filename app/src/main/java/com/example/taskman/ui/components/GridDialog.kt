package com.example.taskman.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.taskman.R

@Composable
fun GridDialog(
    items: List<Any>,
    selectedColor: Color,
    selectedIcon: Int,
    onItemSelected: (Any) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(
        onClick = { showDialog = true }
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(
                id = if (items.first() is Color) R.drawable.ic_circle else selectedIcon
            ),
            tint = selectedColor,
            contentDescription = "showSelected"
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                color = MaterialTheme.colorScheme.inverseOnSurface,
                shape = MaterialTheme.shapes.medium
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(10),
                    modifier = Modifier.padding(16.dp)
                ) {
                    items(items) { item ->
                        IconButton(
                            onClick = {
                                onItemSelected(item)
                                showDialog = false
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(24.dp),
                                painter = painterResource(id = item as? Int ?: selectedIcon),
                                tint = item as? Color ?: selectedColor,
                                contentDescription = "Item"
                            )
                        }
                    }
                }
            }
        }
    }
}