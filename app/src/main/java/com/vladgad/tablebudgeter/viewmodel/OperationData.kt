package com.vladgad.tablebudgeter.viewmodel


data class OperationData (
    var typeOperation: Int,
    var typeAccount: Int,
    var priority: Int,
    var amount: Double,
    var tag: String,
    var message: String,
    var geoStatus : Boolean,
)