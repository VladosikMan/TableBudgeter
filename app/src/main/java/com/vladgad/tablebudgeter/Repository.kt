package com.vladgad.tablebudgeter

import com.vladgad.tablebudgeter.model.OperationRepository
import com.vladgad.tablebudgeter.model.data.StatusOperationEnum
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.OperationStatus
import com.vladgad.tablebudgeter.model.room.BudgeterDataBaseRepository
import com.vladgad.tablebudgeter.model.table.GoogleSheetsDatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Repository : OperationRepository {
    private val _operations = MutableStateFlow<List<Operation>>(emptyList())
    val operations: StateFlow<List<Operation>> = _operations.asStateFlow()

    //класс настройки логиги и работы с источниками данных
    companion object {
        val INSTANCE_REPOSITORY: Repository by lazy { Repository() }
    }

    private val roomDatabase = BudgeterDataBaseRepository()
    private val googleTableDataBase: GoogleSheetsDatabaseRepository =
        GoogleSheetsDatabaseRepository()

    fun updateGoogleToken(token : String){
        googleTableDataBase.updateAccessToken(token)
    }

    override suspend fun insertOperation(operation: Operation): OperationStatus {
        try {
            val statusRoom = roomDatabase.insertOperation(operation)
            val statusGoogle = googleTableDataBase.insertOperation(operation)
            return checkStatusInsert(statusRoom, statusGoogle)
        } catch (e: Exception) {
            e.printStackTrace()
            return OperationStatus.InsertStatus(StatusOperationEnum.FUNC_ERROR.code)
        }
    }

    override suspend fun insertOperations(operations: List<Operation>): OperationStatus {
        val statusRoom = roomDatabase.insertOperations(operations)
        val statusGoogle = googleTableDataBase.insertOperations(operations)
        _operations.update {
            it + operations
        }
        return checkStatusInsert(statusRoom, statusGoogle)
    }

    override suspend fun getOperation(id: Long): OperationStatus {
        val res = googleTableDataBase.getOperation(id)
        return if (res is OperationStatus.SuccessResult)
            OperationStatus.GetOperationsStatus(
                StatusOperationEnum.ALL_REPOSITORY_SUCCESS.code,
                res.listResult
            )
        else
            OperationStatus.GetOperationsStatus(StatusOperationEnum.GOOGLE_ERROR.code, null)
    }

    override suspend fun getAllOperations(): OperationStatus {
        val res = googleTableDataBase.getAllOperations()
        return if (res is OperationStatus.SuccessResult) {
            _operations.update {
                res.listResult
            }
            OperationStatus.GetOperationsStatus(
                StatusOperationEnum.ALL_REPOSITORY_SUCCESS.code,
                res.listResult
            )

        } else
            OperationStatus.GetOperationsStatus(StatusOperationEnum.GOOGLE_ERROR.code, null)
    }

    override suspend fun updateOperation(
        id: Long, operation: Operation
    ): OperationStatus {
        try {
            val statusRoom = googleTableDataBase.updateOperation(id, operation)
            val statusGoogle = roomDatabase.updateOperation(id, operation)
            return checkStatusUpdate(statusRoom, statusGoogle, 1)
        } catch (e: Exception) {
            e.printStackTrace()
            return OperationStatus.UpdateStatus(StatusOperationEnum.FUNC_ERROR.code, 1)
        }
    }

    override suspend fun deleteOperation(id: Long): OperationStatus {
        try {
            val statusRoom = googleTableDataBase.deleteOperation(id)
            val statusGoogle = roomDatabase.deleteOperation(id)
            return checkStatusDelete(statusRoom, statusGoogle)
        } catch (e: Exception) {
            e.printStackTrace()
            return OperationStatus.DeleteStatus(StatusOperationEnum.FUNC_ERROR.code)
        }
    }

//    override suspend fun getOperationsCount(): Int {
//        googleTableDataBase.getOperationsCount()
//        roomDatabase.getOperationsCount()
//    }
//
//    override suspend fun getTotalAmount(): Double {
//        googleTableDataBase.getTotalAmount()
//        roomDatabase.getTotalAmount()
//    }
//
//    override suspend fun isOperationExists(id: Long): Boolean {
//        googleTableDataBase.isOperationExists(id)
//        return roomDatabase.isOperationExists(id)
//    }

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
            return OperationStatus.InsertStatus(StatusOperationEnum.ALL_REPOSITORY_SUCCESS.code)
        if (statusRoom is OperationStatus.Error && statusGoogle is OperationStatus.Success)
            return OperationStatus.InsertStatus(StatusOperationEnum.ROOM_ERROR.code)
        if (statusRoom is OperationStatus.Success && statusGoogle is OperationStatus.Error)
            return OperationStatus.InsertStatus(StatusOperationEnum.GOOGLE_ERROR.code)
        if (statusRoom is OperationStatus.Error && statusGoogle is OperationStatus.Error)
            return OperationStatus.InsertStatus(StatusOperationEnum.INSERT_ERROR.code)
        return OperationStatus.InsertStatus(StatusOperationEnum.FUNC_ERROR.code)
    }

    private fun checkStatusUpdate(
        statusRoom: OperationStatus,
        statusGoogle: OperationStatus,
        updateRows: Int,
    ): OperationStatus {
        if (statusRoom is OperationStatus.Success && statusGoogle is OperationStatus.Success)
            return OperationStatus.UpdateStatus(
                StatusOperationEnum.ALL_REPOSITORY_SUCCESS.code,
                updateRows
            )
        if (statusRoom is OperationStatus.Error && statusGoogle is OperationStatus.Success)
            return OperationStatus.UpdateStatus(StatusOperationEnum.ROOM_ERROR.code, updateRows)
        if (statusRoom is OperationStatus.Success && statusGoogle is OperationStatus.Error)
            return OperationStatus.UpdateStatus(StatusOperationEnum.GOOGLE_ERROR.code, updateRows)
        if (statusRoom is OperationStatus.Error && statusGoogle is OperationStatus.Error)
            return OperationStatus.UpdateStatus(StatusOperationEnum.INSERT_ERROR.code, updateRows)
        return OperationStatus.UpdateStatus(StatusOperationEnum.FUNC_ERROR.code, updateRows)
    }

    private fun checkStatusDelete(
        statusRoom: OperationStatus,
        statusGoogle: OperationStatus
    ): OperationStatus {
        if (statusRoom is OperationStatus.Success && statusGoogle is OperationStatus.Success)
            return OperationStatus.DeleteStatus(StatusOperationEnum.ALL_REPOSITORY_SUCCESS.code)
        if (statusRoom is OperationStatus.Error && statusGoogle is OperationStatus.Success)
            return OperationStatus.DeleteStatus(StatusOperationEnum.ROOM_ERROR.code)
        if (statusRoom is OperationStatus.Success && statusGoogle is OperationStatus.Error)
            return OperationStatus.DeleteStatus(StatusOperationEnum.GOOGLE_ERROR.code)
        if (statusRoom is OperationStatus.Error && statusGoogle is OperationStatus.Error)
            return OperationStatus.DeleteStatus(StatusOperationEnum.INSERT_ERROR.code)
        return OperationStatus.DeleteStatus(StatusOperationEnum.FUNC_ERROR.code)
    }
}