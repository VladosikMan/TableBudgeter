package com.vladgad.tablebudgeter

interface RepositoryInterface {
    fun insertTransaction(transaction: Transaction): Int // возвращает номер строки
}