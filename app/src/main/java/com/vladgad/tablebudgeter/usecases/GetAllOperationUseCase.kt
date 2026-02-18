package com.vladgad.tablebudgeter.usecases


import com.vladgad.tablebudgeter.model.OperationRepository
import com.vladgad.tablebudgeter.model.data.OperationStatus

class GetAllOperationUseCase(private val repository: OperationRepository) {
    suspend operator fun invoke(): OperationStatus {
        return repository.getAllOperations()
    }
}