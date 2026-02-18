package com.vladgad.tablebudgeter.usecases

import com.vladgad.tablebudgeter.Repository
import com.vladgad.tablebudgeter.model.OperationRepository
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.OperationStatus
import kotlinx.coroutines.flow.StateFlow

class GetAllOperationUseCase(private val repository: Repository) {
    suspend operator fun invoke(): OperationStatus {
        return repository.getAllOperations()
    }
}