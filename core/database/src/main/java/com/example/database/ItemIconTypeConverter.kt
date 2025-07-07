package com.example.database

import androidx.room.TypeConverter
import com.example.shared.ItemIcon
import com.example.shared.SystemIcon
import com.example.shared.UserIcon

class ItemIconTypeConverter {

    @TypeConverter
    fun fromString(value: String): ItemIcon {
        return when {
            UserIcon.fromName(value) != null -> UserIcon.fromName(value)!!
            SystemIcon.fromName(value) != null -> SystemIcon.fromName(value)!!
            else -> SystemIcon.Testing
        }
    }

    @TypeConverter
    fun toString(icon: ItemIcon?): String {
        return icon.toString()
    }
}