package com.example.shared

import kotlinx.serialization.Serializable

@Serializable
enum class UserIcon(override val ru: String): ItemIcon {
    Beauty("Красота"),
    Goal("Цель"),
    Grocery("Продукты"),
    Medicine("Медицина"),
    Education("Образование"),
    Home("Дом"),
    Payment("Оплата"),
    Work("Работа"),
    Target("Мишень"),
    Computer("Компьютер"),
    Skillet("Сковорода"),
    Crib("Кроватка"),
    Weekend("Выходные"),
    Checkroom("Гардероб"),
    Toolbox("Инструменты"),
    Sports("Спорт"),
    Hiking("Поход"),
    Favorite("Избранное"),
    Light("Светлая тема"),
    Dark("Темная тема"),
    Cake("Торт"),
    Pets("Питомцы"),
    Schedule("Расписание");

    companion object {
        private val nameToIcon = entries.associateBy { it.name }

        fun fromName(name: String): UserIcon? {
            return nameToIcon[name]
        }
    }
}