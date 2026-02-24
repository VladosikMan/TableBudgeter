package com.vladgad.tablebudgeter.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OperationViewModel : ViewModel() {
    private val _operationData = MutableStateFlow<OperationData>(
        OperationData(
            typeOperation = 0,
            typeAccount = 0, priority = 3, amount = 0.0, tag = "", message = "", geoStatus = false
        )
    )
    val operationData: StateFlow<OperationData> = _operationData.asStateFlow()

    fun updateTypeOperation(type : Int){
        _operationData.update { it.copy(typeOperation = type) }
    }
}
