package com.vladgad.tablebudgeter.view.operation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.vladgad.tablebudgeter.viewmodel.OperationViewModel

@Composable
fun OperationCreator(viewModel: OperationViewModel, modif : Byte) {
    val operationData by viewModel.operationData.collectAsState()
    PaymentRow(
        selectedAccount = operationData.typeAccount,
        selectedPriority = operationData.priority,
        amount = operationData.amount,
        onSelectedAccountChange = { viewModel.updateAccount(it) },
        onSelectedPriorityChange = { viewModel.updatePriority(it) })
    TagMessageGeoRow(
        operationData.tag, operationData.message, operationData.geoStatus,
        onTagChange = { viewModel.updateTag(it) },
        onMessageChange = { viewModel.updateMessage(it) },
        onGeoCheckedChange = { viewModel.updateGeoStatus(it) })
    CalculatorScreen(
        amount = operationData.amount,
        onAmountChange = { viewModel.updateAmount(it) },
        createOperation = { viewModel.insertOperation(modif) })
}
