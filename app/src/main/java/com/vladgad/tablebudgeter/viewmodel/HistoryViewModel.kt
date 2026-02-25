package com.vladgad.tablebudgeter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladgad.tablebudgeter.Repository
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.usecases.GetAllOperationUseCase
import com.vladgad.tablebudgeter.usecases.InsertOperationsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    val operations = Repository.INSTANCE_REPOSITORY.operations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addOperations(operationsList: List<Operation>){
        viewModelScope.launch {
            val result = InsertOperationsUseCase(Repository.INSTANCE_REPOSITORY).invoke(operationsList)
            val x = 12
        }
    }
    fun getAllOperation(){
        viewModelScope.launch {
            val result = GetAllOperationUseCase(Repository.INSTANCE_REPOSITORY).invoke()
        }
    }

}
