package com.vladgad.tablebudgeter.usecases

import com.vladgad.tablebudgeter.Repository
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.OperationStatus

class InsertOperationsUseCase (private val repository: Repository){
    suspend operator fun invoke(operations: List<Operation>): OperationStatus {
        // 1. Бизнес‑валидация
        if (operations.isEmpty()) {
            return OperationStatus.Error("Сумма должна быть положительной")
        }
        return repository.insertOperations(operations)
    }
}