package com.example.ui

import androidx.compose.ui.graphics.Color
import com.example.shared.ItemColor

object ColorMapper {
    fun mapToColor(color: ItemColor): Color = when (color) {
        ItemColor.Red -> Red
        ItemColor.LightGreen -> LightGreen
        ItemColor.Blue -> Blue
        ItemColor.Orange -> Orange
        ItemColor.Purple -> Purple
        ItemColor.LightBlue -> LightBlue
        ItemColor.RedLight -> RedLight
        ItemColor.GreenLight -> GreenLight
        ItemColor.BlueLight -> BlueLight
        ItemColor.OrangeLight -> OrangeLight
        ItemColor.PinkLight -> PinkLight
        ItemColor.GreenVeryLight -> GreenVeryLight
        ItemColor.Teal -> Teal
        ItemColor.DeepPurple -> DeepPurple
        ItemColor.Indigo -> Indigo
        ItemColor.Brown -> Brown
        ItemColor.Cyan -> Cyan
        ItemColor.Amber -> Amber
        ItemColor.Magenta -> Magenta
        ItemColor.Gray -> Gray
        ItemColor.Rose -> Rose
        ItemColor.Navy -> Navy
        ItemColor.Mint -> Mint
        ItemColor.Black -> Color.Black
    }
}