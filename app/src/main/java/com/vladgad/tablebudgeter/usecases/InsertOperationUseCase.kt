package com.vladgad.tablebudgeter.usecases

import com.vladgad.tablebudgeter.Repository
import com.vladgad.tablebudgeter.model.OperationRepository
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.OperationStatus

class InsertOperationUseCase (private val repository: OperationRepository){
    suspend operator fun invoke(operation: Operation): OperationStatus {
        // 1. Бизнес‑валидация
        if (operation.amount <= 0) {
            return OperationStatus.Error("Сумма должна быть положительной")
        }
        if (operation.typeOperation.isBlank()) {
            return OperationStatus.Error("Заголовок не может быть пустым")
        }
        return repository.insertOperation(operation)
    }
}