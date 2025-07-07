package com.example.ui.components

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import com.example.shared.ItemColor
import com.example.shared.ItemIcon
import com.example.ui.ColorMapper
import com.example.ui.IconMapper
import com.example.ui.R


@Composable
fun ColorGridDialog(
    colors: List<ItemColor>,
    selectedColor: ItemColor,
    onColorSelected: (ItemColor) -> Unit
) {
    GridDialog(
        items = colors,
        selectedItem = selectedColor,
        onItemSelected = onColorSelected
    ) { itemColor ->
        Icon(
            painter = painterResource(id = R.drawable.ic_circle),
            contentDescription = "Color",
            tint = ColorMapper.mapToColor(itemColor)
        )
    }
}

@Composable
fun IconGridDialog(
    icons: List<ItemIcon>,
    selectedIcon: ItemIcon,
    selectedColor: ItemColor,
    onIconSelected: (ItemIcon) -> Unit
) {
    GridDialog(
        items = icons,
        selectedItem = selectedIcon,
        onItemSelected = onIconSelected
    ) { itemIcon ->
        Icon(
            painter = painterResource(id = IconMapper.itemIconToDrawable(itemIcon)),
            contentDescription = "Icon",
            tint = ColorMapper.mapToColor(selectedColor)
        )
    }
}

@Composable
fun <T> GridDialog(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemContent: @Composable (T) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { showDialog = true }) {
        itemContent(selectedItem)
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(shape = MaterialTheme.shapes.medium) {
                LazyVerticalGrid(columns = GridCells.Fixed(8)) {
                    items(items) { item ->
                        IconButton(
                            onClick = {
                                onItemSelected(item)
                                showDialog = false
                            }
                        ) {
                            itemContent(item)
                        }
                    }
                }
            }
        }
    }
}