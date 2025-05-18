package com.example.taskman.ui.utils

import com.example.taskman.model.ItemIcon
import com.example.taskman.ui.theme.Blue
import com.example.taskman.ui.theme.BlueLight
import com.example.taskman.ui.theme.GreenLight
import com.example.taskman.ui.theme.GreenVeryLight
import com.example.taskman.ui.theme.LightBlue
import com.example.taskman.ui.theme.LightGreen
import com.example.taskman.ui.theme.OrangeLight
import com.example.taskman.ui.theme.PinkLight
import com.example.taskman.ui.theme.Purple
import com.example.taskman.ui.theme.Red
import com.example.taskman.ui.theme.RedLight
import com.example.taskman.ui.theme.Yellow

object TaskManAppData {
    val icons = ItemIcon.entries.map { it.id }

    val colors = listOf(
        Red,
        LightGreen,
        Blue,
        Yellow,
        Purple,
        LightBlue,
        RedLight,
        GreenLight,
        BlueLight,
        OrangeLight,
        PinkLight,
        GreenVeryLight
    )
}
