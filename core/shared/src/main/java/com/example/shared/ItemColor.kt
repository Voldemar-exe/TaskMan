package com.example.shared

import kotlinx.serialization.Serializable

@Serializable
enum class ItemColor {
    Red, LightGreen, Blue,
    Orange, Purple, LightBlue,
    RedLight, GreenLight, BlueLight,
    OrangeLight, PinkLight, GreenVeryLight,
    Teal, DeepPurple, Indigo,
    Brown, Cyan, Amber,
    Magenta, Gray, Rose,
    Navy, Mint, Black;
}