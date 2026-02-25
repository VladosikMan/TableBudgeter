package com.vladgad.tablebudgeter.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OperationViewModel : ViewModel() {
    private val _operationData = MutableStateFlow<OperationData>(
        OperationData(
            typeOperation = -1,
            typeAccount = 0, priority = 2, amount = "0", tag = "", message = "", geoStatus = false
        )
    )
    val operationData: StateFlow<OperationData> = _operationData.asStateFlow()
    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    fun openBottomSheet() {
        _showBottomSheet.value = true
    }

    fun closeBottomSheet() {
        _showBottomSheet.value = false
    }

    fun updateTypeOperation(type: Int) {
        _operationData.update { it.copy(typeOperation = type) }
    }

    fun updateAccount(type: Int) {
        _operationData.update { it.copy(typeAccount = type) }
    }

    fun updatePriority(type: Int) {
        _operationData.update { it.copy(priority = type) }
    }

    fun updateTag(tag: String) {
        _operationData.update { it.copy(tag = tag) }
    }

    fun updateMessage(message: String) {
        _operationData.update { it.copy(message = message) }
    }

    fun updateGeoStatus(geoStatus: Boolean) {
        _operationData.update { it.copy(geoStatus = geoStatus) }
    }

    fun updateAmount(amount: String) {
        _operationData.update { it.copy(amount = amount) }
    }
}
