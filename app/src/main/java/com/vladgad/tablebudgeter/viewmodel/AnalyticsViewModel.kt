package com.vladgad.tablebudgeter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladgad.tablebudgeter.Repository
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.usecases.GetAllOperationUseCase
import com.vladgad.tablebudgeter.usecases.InsertOperationsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnalyticsViewModel : ViewModel() {
    init{
        getAllOperation()
    }
    val operations = Repository.INSTANCE_REPOSITORY.operations.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getAllOperation() {
        viewModelScope.launch {
            delay(5000L)
            val result = GetAllOperationUseCase(Repository.INSTANCE_REPOSITORY).invoke()
            val x = 12
        }
    }
}
