package com.vladgad.tablebudgeter.usecases

import com.vladgad.tablebudgeter.Repository
import com.vladgad.tablebudgeter.model.data.OperationStatus

class GetOperationUseCase (private val repository: Repository){
    suspend operator fun invoke(id: Long): OperationStatus {
        return repository.getOperation(id)
    }
}