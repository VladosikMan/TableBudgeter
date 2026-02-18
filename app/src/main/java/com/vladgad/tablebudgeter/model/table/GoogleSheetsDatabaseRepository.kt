package com.vladgad.tablebudgeter.model.table

import com.vladgad.tablebudgeter.model.OperationRepository
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.OperationStatus
import com.vladgad.tablebudgeter.utils.Constants.GOOGLE_SHEET_ID
import com.vladgad.tablebudgeter.utils.Constants.GOOGLE_SHEET_ID_PAGE

class GoogleSheetsDatabaseRepository() : OperationRepository {

    private val spreadsheetId: String = GOOGLE_SHEET_ID
    private val sheetId: Long = GOOGLE_SHEET_ID_PAGE

    private val database: SheetsServiceHelper by lazy {
        SheetsServiceHelper.INSTANCE_SHEET_HELPER
    }

    fun updateAccessToken(token: String) {
        database.updateAccessToken(token)
    }

    override suspend fun insertOperation(operation: Operation): OperationStatus {
        return try {
            if (database.addDataRows(spreadsheetId, sheetId, 1, listOf(operation)))
                OperationStatus.Success(1)
            else
                OperationStatus.Error("", 0)
        } catch (e: Exception) {
            OperationStatus.Error(e.message ?: "Insert failed")
        }
    }

    override suspend fun insertOperations(operations: List<Operation>): OperationStatus {
        return try {
            if (database.addDataRows(spreadsheetId, sheetId, 1, operations))
                OperationStatus.Success(1)
            else
                OperationStatus.Error("", 0)
        } catch (e: Exception) {
            OperationStatus.Error(e.message ?: "Insert failed")
        }
    }

    override suspend fun getOperation(id: Long): OperationStatus {
        return try {
            val operation = database.getRowById(spreadsheetId, sheetId, id)
            OperationStatus.SuccessResult(operation.id, listOf(operation))
        } catch (e: Exception) {
            OperationStatus.Error(e.message ?: "Insert failed")
        }
    }

    override suspend fun getAllOperations(): OperationStatus {
        return try {
            val operations = database.getAllRows(spreadsheetId, sheetId)
            OperationStatus.SuccessResult(0, operations)
        } catch (e: Exception) {
            OperationStatus.Error(e.message ?: "Insert failed")
        }
    }

    override suspend fun updateOperation(
        id: Long,
        operation: Operation
    ): OperationStatus {
        return try {
            val success = database.updateRowById(spreadsheetId, sheetId, id, operation)
            if (success)
                OperationStatus.Success(1)
            else
                OperationStatus.Error("Ошибка обновления",0)
        } catch (e: Exception) {
            OperationStatus.Error(e.message ?: "Insert failed")
        }
    }

    override suspend fun deleteOperation(id: Long): OperationStatus {
        return try {
            val success = database.deleteRowById(spreadsheetId, sheetId, id)
            if (success)
                OperationStatus.Success(1)
            else
                OperationStatus.Error("Ошибка удаления",0)
        } catch (e: Exception) {
            OperationStatus.Error(e.message ?: "Insert failed")
        }
    }
/*
    override suspend fun getOperationsCount(): Int {
        return 1
    }

    override suspend fun getTotalAmount(): Double {
        return 1.0
    }

    override suspend fun isOperationExists(id: Long): Boolean {
        return true
    }*/
}
