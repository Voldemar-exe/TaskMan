package com.example.ui

import androidx.annotation.DrawableRes
import com.example.shared.ItemIcon
import com.example.shared.SystemIcon
import com.example.shared.UserIcon

object IconMapper {

    @DrawableRes
    fun itemIconToDrawable(icon: ItemIcon): Int = when (icon) {
        UserIcon.Beauty -> R.drawable.ic_beauty
        UserIcon.Goal -> R.drawable.ic_goal
        UserIcon.Grocery -> R.drawable.ic_grocery
        UserIcon.Medicine -> R.drawable.ic_medicine
        UserIcon.Education -> R.drawable.ic_education
        UserIcon.Home -> R.drawable.ic_home
        UserIcon.Payment -> R.drawable.ic_payment
        UserIcon.Work -> R.drawable.ic_work
        UserIcon.Target -> R.drawable.ic_target
        UserIcon.Computer -> R.drawable.ic_computer
        UserIcon.Skillet -> R.drawable.ic_skillet
        UserIcon.Crib -> R.drawable.ic_crib
        UserIcon.Weekend -> R.drawable.ic_weekend
        UserIcon.Checkroom -> R.drawable.ic_checkroom
        UserIcon.Toolbox -> R.drawable.ic_toolbox
        UserIcon.Sports -> R.drawable.ic_sports
        UserIcon.Hiking -> R.drawable.ic_hiking
        UserIcon.Favorite -> R.drawable.ic_favorite
        UserIcon.Light -> R.drawable.ic_light_mode
        UserIcon.Dark -> R.drawable.ic_dark_mode
        UserIcon.Cake -> R.drawable.ic_cake
        UserIcon.Pets -> R.drawable.ic_pets
        UserIcon.Schedule -> R.drawable.ic_schedule

        SystemIcon.Amount -> R.drawable.ic_amount
        SystemIcon.Filter -> R.drawable.ic_filter
        SystemIcon.Sync -> R.drawable.ic_sync

        SystemIcon.Testing -> R.drawable.ic_testing
    }
}