package com.example.myapplication.data

/**
 * Класс, представляющий группу спортсменов
 * @property name - имя группы
 * @author Севастьянов Иван
 */
data class Group(var name: String, var itemId: String, var members: MutableList<String>)
