package com.vladgad.tablebudgeter

import java.time.LocalDate

data class Transaction(
    val operation: String, // "Продукты", "Кафе", "Зарплата"
    val amount: Double,
    val account: String, // "Наличные", "Карта", "Сбережения"
    val date: LocalDate = LocalDate.now(),
)
