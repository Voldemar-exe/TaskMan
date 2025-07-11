package com.example.shared

import kotlinx.serialization.Serializable

@Serializable
enum class SystemIcon(override val ru: String) : ItemIcon {
    Amount("Количество"),
    Filter("Фильтр"),
    Sync("Синхронизация"),
    Testing("Тестовая ошибка"),
    CalendarEvent("Событие"),
    Tag("Метка");

    companion object {
        private val nameToIcon = entries.associateBy { it.name }

        fun fromName(name: String): SystemIcon? {
            return nameToIcon[name]
        }
    }
}