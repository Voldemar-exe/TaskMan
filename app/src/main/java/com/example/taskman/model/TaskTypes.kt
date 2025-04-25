package com.example.taskman.model

enum class TaskTypes(
    val note: String
) {
    Work("Рабочие задачи и проекты"),
    Personal("Личные дела и мероприятия"),
    Home("Домашние обязанности"),
    Health("Здоровье и фитнес"),
    Education("Обучение и развитие"),
    Goal("Цель"),
    Shopping("Покупки и списки");
}