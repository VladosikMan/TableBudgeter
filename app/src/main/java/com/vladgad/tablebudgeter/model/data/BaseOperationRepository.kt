package com.vladgad.tablebudgeter.model.data

abstract class BaseOperationRepository : OperationRepository {

    protected val operations = mutableListOf<Operation>()
    override suspend fun getOperationsCount(): Int = operations.size

    override suspend fun getTotalAmount(): Double =
        operations.sumOf { it.amount }

    override suspend fun isOperationExists(id: Long): Boolean =
        operations.any { it.id == id }
}
