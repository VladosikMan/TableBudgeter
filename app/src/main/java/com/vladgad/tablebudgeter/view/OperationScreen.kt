package com.vladgad.tablebudgeter.view


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vladgad.tablebudgeter.view.data.ChipElement
import com.vladgad.tablebudgeter.view.operation.CategoryGridSelector
import com.vladgad.tablebudgeter.view.operation.OperationBottom
import com.vladgad.tablebudgeter.viewmodel.OperationViewModel
import kotlinx.coroutines.launch
import kotlin.math.E


const val EXPENCE_MODIF: Byte = -1
const val INCOME_MODIF: Byte = 1

@Composable
fun OperationScreen(viewModel: OperationViewModel = viewModel()) {
    CustomTabsScreen(viewModel)
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTabsScreen(viewModel: OperationViewModel) {
    val tabs = listOf("Расход", "Доход", "Перевод")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        // Кастомная панель вкладок с рамкой
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp) // внешний отступ, чтобы рамка не прилипала к краям
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp)) // чтобы содержимое не выходило за рамку
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center // или Start, в зависимости от дизайна
            ) {
                itemsIndexed(tabs) { index, title ->
                    TabButton(
                        title = title,
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        modifier = Modifier
                            .width(90.dp) // фиксированная ширина для узких кнопок
                            .padding(vertical = 8.dp) // отступы сверху/снизу
                    )
                }
            }
        }

        // Контент вкладок с поддержкой свайпа
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> OperationElement(viewModel, viewModel.expenseCategories, EXPENCE_MODIF)
                1 -> OperationElement(viewModel, viewModel.incomeCategories, INCOME_MODIF)
                2 ->  TransferScreen(viewModel)
            }
        }
    }
}

@Composable
fun TabButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        // Индикатор выбранной вкладки (подчёркивание)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(if (selected) 1f else 0f)
                .height(2.dp)
                .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(viewModel: OperationViewModel) {
    val accounts = listOf("Т-Банк", "Сбер", "ВТБ", "Наличка")

    var fromAccount by remember { mutableStateOf(accounts[0]) }
    var toAccount by remember { mutableStateOf(accounts[1]) }
    var amount by remember { mutableStateOf("") }

    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок
        Text(
            text = "Перевод между счетами",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Карточка с основными элементами
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Строка выбора счетов
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Выбор отправителя
                    ExposedDropdownMenuBox(
                        expanded = fromExpanded,
                        onExpandedChange = { fromExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        TextField(
                            value = fromAccount,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = fromExpanded,
                            onDismissRequest = { fromExpanded = false }
                        ) {
                            accounts.forEach { account ->
                                DropdownMenuItem(
                                    text = { Text(account) },
                                    onClick = {
                                        fromAccount = account
                                        fromExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Стрелка
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Выбор получателя
                    ExposedDropdownMenuBox(
                        expanded = toExpanded,
                        onExpandedChange = { toExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        TextField(
                            value = toAccount,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = toExpanded,
                            onDismissRequest = { toExpanded = false }
                        ) {
                            accounts.forEach { account ->
                                DropdownMenuItem(
                                    text = { Text(account) },
                                    onClick = {
                                        toAccount = account
                                        toExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Поле ввода суммы
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Сумма") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Text("₽") }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка перевода
        Button(
            onClick = {
                viewModel.transferOperation(fromAccount, toAccount, amount)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = amount.isNotBlank() && fromAccount != toAccount
        ) {
            Text("Перевести")
        }
    }
}
