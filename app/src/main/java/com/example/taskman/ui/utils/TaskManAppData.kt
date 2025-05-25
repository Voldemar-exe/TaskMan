package com.example.taskman.ui.utils

import com.example.taskman.ui.theme.Amber
import com.example.taskman.ui.theme.Blue
import com.example.taskman.ui.theme.BlueLight
import com.example.taskman.ui.theme.Brown
import com.example.taskman.ui.theme.Cyan
import com.example.taskman.ui.theme.DeepPurple
import com.example.taskman.ui.theme.Gray
import com.example.taskman.ui.theme.GreenLight
import com.example.taskman.ui.theme.GreenVeryLight
import com.example.taskman.ui.theme.Indigo
import com.example.taskman.ui.theme.LightBlue
import com.example.taskman.ui.theme.LightGreen
import com.example.taskman.ui.theme.Lime
import com.example.taskman.ui.theme.Magenta
import com.example.taskman.ui.theme.Mint
import com.example.taskman.ui.theme.Navy
import com.example.taskman.ui.theme.Orange
import com.example.taskman.ui.theme.OrangeLight
import com.example.taskman.ui.theme.PinkLight
import com.example.taskman.ui.theme.Purple
import com.example.taskman.ui.theme.Red
import com.example.taskman.ui.theme.RedLight
import com.example.taskman.ui.theme.Rose
import com.example.taskman.ui.theme.Teal

object TaskManAppData {
    val icons = ItemIcon.entries.map { it.id }

    val colors = listOf(
        Red, LightGreen, Blue,
        Orange, Purple, LightBlue,
        RedLight, GreenLight, BlueLight,
        OrangeLight, PinkLight, GreenVeryLight,
        Teal, DeepPurple, Lime,
        Indigo, Brown, Cyan,
        Amber, Magenta, Gray,
        Rose, Navy, Mint
    )
}
