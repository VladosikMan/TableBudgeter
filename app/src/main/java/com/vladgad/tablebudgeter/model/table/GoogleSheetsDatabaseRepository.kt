package com.vladgad.tablebudgeter.model.table

import com.vladgad.tablebudgeter.model.BaseOperationRepository
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.OperationStatus
import com.vladgad.tablebudgeter.model.room.OperationExtensions.Companion.toEntity

class GoogleSheetsDatabaseRepository( ): BaseOperationRepository() {

    private lateinit var  spreadsheetId : String
    private var sheetId: Long = -1

    fun setId(spreadsheetId : String,sheetId: Long ){
        this.spreadsheetId = spreadsheetId
        this.sheetId = sheetId
    }
    private val database: SheetsServiceHelper by lazy {
        SheetsServiceHelper.getInstance()
    }

    fun updateAccessToken(token : String){
        database.updateAccessToken(token)
    }
    override suspend fun insertOperation(operation: Operation): OperationStatus {
        return try {
            val id = database.addDataRows(spreadsheetId, sheetId, 1 , listOf(operation))
            OperationStatus.Success(id)
        } catch (e: Exception) {
            OperationStatus.Error(e.message ?: "Insert failed")
        }
    }

    override suspend fun insertOperations(operations: List<Operation>): OperationStatus {
        return try {
            val id = database.addDataRows(spreadsheetId, sheetId, 1 , operations)
            OperationStatus.Success(id)
        } catch (e: Exception) {
            OperationStatus.Error(e.message ?: "Insert failed")
        }
    }

    override suspend fun getOperation(id: Long): OperationStatus {
        return try {
            val operation  = database.getRowById(spreadsheetId, sheetId, id)
            OperationStatus.SuccessResult(operation.id, listOf(operation))
        } catch (e: Exception) {
            OperationStatus.Error(e.message ?: "Insert failed")
        }
    }

    override suspend fun getAllOperations(): OperationStatus {
        return try {
            val operations  = database.getAllRows(spreadsheetId, sheetId)
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
            val success  = database.updateRowById(spreadsheetId, sheetId, id, operation)
            if (success)
                OperationStatus.SuccessUpdateDelete(0)
            else
                OperationStatus.SuccessUpdateDelete(-1)
        } catch (e: Exception) {
            OperationStatus.Error(e.message ?: "Insert failed")
        }
    }

    override suspend fun deleteOperation(id: Long): OperationStatus {
        return try {
            val success  = database.deleteRowById(spreadsheetId, sheetId, id)
            if (success)
                OperationStatus.SuccessUpdateDelete(0)
            else
                OperationStatus.SuccessUpdateDelete(-1)
        } catch (e: Exception) {
            OperationStatus.Error(e.message ?: "Insert failed")
        }
    }
}