package com.vladgad.tablebudgeter.model

import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.OperationStatus
import com.vladgad.tablebudgeter.model.room.BudgeterDataBaseRepository
import com.vladgad.tablebudgeter.model.table.GoogleSheetsDatabaseRepository

class Repository : OperationRepository {
    //класс настройки логиги и работы с источниками данных
    private val roomDatabase = BudgeterDataBaseRepository()
    private val googleTableDataBase: GoogleSheetsDatabaseRepository =
        GoogleSheetsDatabaseRepository()

    override suspend fun insertOperation(operation: Operation): OperationStatus {
        roomDatabase.insertOperation(operation)
        googleTableDataBase.insertOperation(operation)
    }

    override suspend fun insertOperations(operations: List<Operation>): OperationStatus {
        roomDatabase.insertOperations(operations)
        googleTableDataBase.insertOperations(operations)
    }

    override suspend fun getOperation(id: Long): OperationStatus {
        googleTableDataBase.getOperation(id)
        return roomDatabase.getOperation(id)
    }

    override suspend fun getAllOperations(): OperationStatus {
        googleTableDataBase.getAllOperations()
        return roomDatabase.getAllOperations()
    }

    override suspend fun updateOperation(
        id: Long, operation: Operation
    ): OperationStatus {
        googleTableDataBase.updateOperation(id, operation)
        roomDatabase.updateOperation(id, operation)
    }

    override suspend fun deleteOperation(id: Long): OperationStatus {
        googleTableDataBase.deleteOperation(id)
        roomDatabase.deleteOperation(id)
    }

    override suspend fun getOperationsCount(): Int {
        googleTableDataBase.getOperationsCount()
        roomDatabase.getOperationsCount()
    }

    override suspend fun getTotalAmount(): Double {
        googleTableDataBase.getTotalAmount()
        roomDatabase.getTotalAmount()
    }

    override suspend fun isOperationExists(id: Long): Boolean {
        googleTableDataBase.isOperationExists(id)
        return roomDatabase.isOperationExists(id)
    }

    //получить оба списка как два множества
    // перемножить первое со вторым и наоборот
    //получаем два разнящихся поднмножемства и insert их по местам

    suspend fun synchronizeRepository() {
        val googleList = googleTableDataBase.getAllOperations().let {
            val res = it as OperationStatus.SuccessResult
            res.listResult
        }
        val roomList = roomDatabase.getAllOperations().let {
            val res = it as OperationStatus.SuccessResult
            res.listResult
        }
        val googleSet = googleList.associateBy { it.id }
        val roomSet = roomList.associateBy { it.id }
        val googleOnlyIds = googleSet.keys - roomSet.keys
        val roomOnlyIds = roomSet.keys - googleSet.keys
        val toAddToRoom = googleOnlyIds.mapNotNull { googleSet[it] }
        val toAddToGoogle = roomOnlyIds.mapNotNull { roomSet[it] }
        roomDatabase.insertOperations(toAddToRoom)
        googleTableDataBase.insertOperations(toAddToGoogle)
    }
}

