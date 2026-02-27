package com.vladgad.tablebudgeter.view.operation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.vladgad.tablebudgeter.viewmodel.OperationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationBottom(viewModel: OperationViewModel) {
    val showBottomSheet by viewModel.showBottomSheet.collectAsState()
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeBottomSheet() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            OperationCreator(viewModel)
        }
    }
}

