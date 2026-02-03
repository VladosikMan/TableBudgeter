package com.vladgad.tablebudgeter.model.data

sealed class OperationStatus {

    data class Success(val id: Long) : OperationStatus()
    data class SuccessUpdateDelete(val count: Int) : OperationStatus()

    data class SuccessResult(val id: Long, val listResult: List<Operation>) : OperationStatus()
    data class NotFound(val id: Long) : OperationStatus()
    data class Error(val message: String, val code: Int = 0) : OperationStatus()

}