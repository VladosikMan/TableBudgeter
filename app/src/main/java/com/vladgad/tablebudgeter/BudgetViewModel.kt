package com.vladgad.tablebudgeter

import com.vladgad.tablebudgeter.RepositoryInterface

class BudgetViewModel(private val repositoryMock: RepositoryInterface) {
    fun addTransaction(transaction: Transaction): Int {
        repositoryMock.insertTransaction(transaction)
        return 1
    }
}