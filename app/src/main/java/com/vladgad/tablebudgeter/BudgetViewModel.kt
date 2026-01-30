package com.vladgad.tablebudgeter

class BudgetViewModel(private val repositoryMock: RepositoryInterface) {
    fun addTransaction(transaction: Transaction): Int {
        repositoryMock.insertTransaction(transaction)
        return 1
    }
}