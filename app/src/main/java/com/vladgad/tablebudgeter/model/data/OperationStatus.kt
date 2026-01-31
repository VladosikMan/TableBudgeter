package com.vladgad.tablebudgeter.model.data

sealed class OperationStatus {

    data class Success(val id: Long) : OperationStatus()
    data class SuccessResult<T>(val id: Long, val listResult: List<T>) : OperationStatus()
    data class NotFound(val id: Long) : OperationStatus()
    data class Error(val message: String, val code: Int = 0) : OperationStatus()

}

