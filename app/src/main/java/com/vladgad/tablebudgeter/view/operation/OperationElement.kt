package com.vladgad.tablebudgeter.view.operation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.vladgad.tablebudgeter.view.data.ChipElement
import com.vladgad.tablebudgeter.viewmodel.OperationViewModel

@Composable
fun OperationElement(viewModel: OperationViewModel, categories: List<ChipElement>, modif: Byte) {
    val operationData by viewModel.operationData.collectAsState()
    val statusInsertOperation by viewModel.statusInsertOperation.collectAsState()
    if (statusInsertOperation) {
        val context = LocalContext.current
        Toast.makeText(context, "Операция прошла успешно", Toast.LENGTH_SHORT).show()
        viewModel.updateOperationStatus()
    }
    CategoryGridSelector(
        selectedIndex = operationData.typeOperation,
        categories,
        onTypeOperationChange = {
            viewModel.updateTypeOperation(it)
            viewModel.openBottomSheet()
        }
    )
    OperationBottom(viewModel, modif)
}
