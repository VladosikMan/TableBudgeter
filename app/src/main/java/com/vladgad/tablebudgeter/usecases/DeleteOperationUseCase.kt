package com.vladgad.tablebudgeter.usecases

import com.vladgad.tablebudgeter.Repository
import com.vladgad.tablebudgeter.model.data.OperationStatus

class DeleteOperationUseCase(private val repository: Repository) {
    suspend operator fun invoke(id: Long): OperationStatus {
        return repository.deleteOperation(id)
    }
}
