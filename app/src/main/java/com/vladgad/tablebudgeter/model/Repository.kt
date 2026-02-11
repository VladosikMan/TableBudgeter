package com.vladgad.tablebudgeter.model

import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.OperationStatus
import com.vladgad.tablebudgeter.model.room.BudgeterDataBase
import com.vladgad.tablebudgeter.model.table.GoogleSheetsDatabaseRepository

class Repository  : OperationRepository {
    //класс настройки логиги и работы с источниками данных
    private val roomDatabase = BudgeterDataBase.getInstance()
    private val googleTableDataBase: GoogleSheetsDatabaseRepository = GoogleSheetsDatabaseRepository()

    override suspend fun insertOperation(operation: Operation): OperationStatus {
        TODO("Not yet implemented")
    }

    override suspend fun insertOperations(operations: List<Operation>): OperationStatus {
        TODO("Not yet implemented")
    }

    override suspend fun getOperation(id: Long): OperationStatus {
        TODO("Not yet implemented")
    }

    override suspend fun getAllOperations(): OperationStatus {
        TODO("Not yet implemented")
    }

    override suspend fun updateOperation(
        id: Long,
        operation: Operation
    ): OperationStatus {
        TODO("Not yet implemented")
    }

    override suspend fun deleteOperation(id: Long): OperationStatus {
        TODO("Not yet implemented")
    }

    override suspend fun getOperationsCount(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getTotalAmount(): Double {
        TODO("Not yet implemented")
    }

    override suspend fun isOperationExists(id: Long): Boolean {
        TODO("Not yet implemented")
    }


}

