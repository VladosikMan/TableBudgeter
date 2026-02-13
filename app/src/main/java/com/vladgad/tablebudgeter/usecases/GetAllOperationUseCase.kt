package com.vladgad.tablebudgeter.usecases

import com.vladgad.tablebudgeter.Repository
import com.vladgad.tablebudgeter.model.data.OperationStatus

class GetAllOperationUseCase(private val repository: Repository) {
    suspend operator fun invoke(): OperationStatus {
        return repository.getAllOperations()
    }
}