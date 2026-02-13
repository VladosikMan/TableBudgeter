package com.vladgad.tablebudgeter

import com.vladgad.tablebudgeter.model.OperationRepository
import com.vladgad.tablebudgeter.model.data.InsertStatusOperationEnum
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
        try {
            val statusRoom = roomDatabase.insertOperation(operation)
            val statusGoogle = googleTableDataBase.insertOperation(operation)
            return checkStatusInsert(statusRoom, statusGoogle)
        } catch (e: Exception) {
            e.printStackTrace()
            return OperationStatus.InsertStatus(InsertStatusOperationEnum.FUNC_ERROR.code)
        }
    }

    override suspend fun insertOperations(operations: List<Operation>): OperationStatus {
        val statusRoom = roomDatabase.insertOperations(operations)
        val statusGoogle = googleTableDataBase.insertOperations(operations)
        return checkStatusInsert(statusRoom, statusGoogle)
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

    private fun checkStatusInsert(
        statusRoom: OperationStatus,
        statusGoogle: OperationStatus
    ): OperationStatus {
        if (statusRoom is OperationStatus.Success && statusGoogle is OperationStatus.Success)
            return OperationStatus.InsertStatus(InsertStatusOperationEnum.ALL_REPOSITORY_SUCCESS.code)
        if (statusRoom is OperationStatus.Error && statusGoogle is OperationStatus.Success)
            return OperationStatus.InsertStatus(InsertStatusOperationEnum.ROOM_ERROR.code)
        if (statusRoom is OperationStatus.Success && statusGoogle is OperationStatus.Error)
            return OperationStatus.InsertStatus(InsertStatusOperationEnum.GOOGLE_ERROR.code)
        if (statusRoom is OperationStatus.Error && statusGoogle is OperationStatus.Error)
            return OperationStatus.InsertStatus(InsertStatusOperationEnum.INSERT_ERROR.code)
        return OperationStatus.InsertStatus(InsertStatusOperationEnum.FUNC_ERROR.code)
    }
}