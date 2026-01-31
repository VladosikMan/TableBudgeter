package com.vladgad.tablebudgeter.model

import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.OperationStatus

interface OperationRepository {


    //добавление операции
    suspend fun insertOperation(operation: Operation): OperationStatus
    suspend fun insertOperations(operations: List<Operation>): OperationStatus

    //прочитать список операций
    suspend fun getOperation(id: Long): OperationStatus
    suspend fun getAllOperations(): OperationStatus
    // suspend fun getOperationsByDateRange(from: String, to: String): OperationStatus
    // suspend fun getOperationsByType(type: String): OperationStatus


    suspend fun updateOperation(id: Long, operation: Operation): OperationStatus


    suspend fun deleteOperation(id: Long): OperationStatus
    //  suspend fun deleteOperations(ids: List<Long>): Int // Кол-во удаленных

    // UTILS
    suspend fun getOperationsCount(): Int
    suspend fun getTotalAmount(): Double
    suspend fun isOperationExists(id: Long): Boolean
}