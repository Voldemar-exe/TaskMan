package com.example.taskman.model

enum class TaskType(
    val ru: String,
    val note: String
) {
    Work("Работа", "Рабочие задачи и проекты"),
    Personal("Личное", "Личные дела и мероприятия"),
    Home("Дом", "Домашние обязанности"),
    Health("Здоровье", "Здоровье и фитнес"),
    Education("Образование", "Обучение и развитие"),
    Goal("Цель", "Поставленная цель"),
    Shopping("Траты", "Покупки и списки");
}