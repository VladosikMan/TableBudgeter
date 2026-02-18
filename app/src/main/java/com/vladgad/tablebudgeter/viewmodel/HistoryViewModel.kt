package com.vladgad.tablebudgeter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladgad.tablebudgeter.Repository
import com.vladgad.tablebudgeter.usecases.GetAllOperationUseCase
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

    fun getAllOperation(){
        viewModelScope.launch {
            val result = GetAllOperationUseCase(Repository.INSTANCE_REPOSITORY).invoke()

            val x = 12
        }


    }

}
