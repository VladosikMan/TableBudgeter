package com.vladgad.tablebudgeter.view


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Iso
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vladgad.tablebudgeter.model.data.Operation
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    // Тестовые данные для списка
    val operations = remember {
        List(50) { index ->
               Operation(
                   id = index.toLong(),
                   typeOperation = "type",
                   message = "Операция №$index",
                   amount = (index * 100).toDouble(),
                   dateOperation = 0,
                   account = "sdsd",
               )
        }
    }

    Scaffold(
        topBar = {
            historyAppBar()
        }
    ) { paddingValues ->
        // Используем Column, чтобы разместить LazyColumn и кнопку вертикально
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Список операций
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(operations) { operation ->
                    OperationItem(operation)
                }
            }

            // Кнопка внизу с отступами
            Button(
                onClick = { /* Обработка нажатия */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Нажми меня")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
@Composable
fun historyAppBar(){
    Column {
        CenterAlignedTopAppBar(
            navigationIcon = { StatItem(title = "2026", value = "фев.⇓") },
            title = { Text("История") },
            actions = {
                IconButton(onClick = { /* Поиск */ }) {
                    Icon(Icons.Filled.Search, contentDescription = "Поиск")
                }
                IconButton(onClick = { /* Календарь */ }) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = "Календарь")
                }
            }
        )
        StatisticsRow()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSimpleScreen() {
    HistoryScreen()
}

@Composable
fun StatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
@Preview
@Composable
fun StatisticsRow(
    expenses: String = "0",
    incomes: String = "0",
    balance: String = "0"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        StatItem(title = "Расходы", value = expenses)
        StatItem(title = "Доходы", value = incomes)
        StatItem(title = "Баланс", value = balance)
    }
}

@Preview
@Composable
fun MyMonthDatePicker() {
    val state = rememberDatePickerState()
    var showDialog by remember { mutableStateOf(false) }

    Column {
        Button(onClick = { showDialog = true }) {
            Text("Select Month")
        }
        // Display the selected month and year
        Text("Selected date: ${state.selectedDateMillis?.let {
            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(it)
        } ?: "None"}")
    }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Handle the selected date, you can get the month/year here
                        val selectedDateMillis = state.selectedDateMillis
                        if (selectedDateMillis != null) {
                            // Extract month and year from selectedDateMillis
                            // ...
                        }
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearPickerDialog(
    initialYear: Int = 2000,
    initialMonth: Int = 1,
    onDismiss: () -> Unit = {},
   /* onConfirm: (year: Int, month: Int) -> Unit*/
) {
    var selectedYear by remember { mutableStateOf(initialYear) }
    var selectedMonth by remember { mutableStateOf(initialMonth) }
    var yearDropdownExpanded by remember { mutableStateOf(false) }
    var monthDropdownExpanded by remember { mutableStateOf(false) }

    val years = (2020..2030).toList()
    val months = (1..12).toList()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите месяц") },
        text = {
            Column {
                // Выбор года
                ExposedDropdownMenuBox(
                    expanded = yearDropdownExpanded,
                    onExpandedChange = { yearDropdownExpanded = it }
                ) {
                    TextField(
                        value = selectedYear.toString(),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { TrailingIcon(expanded = yearDropdownExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = yearDropdownExpanded,
                        onDismissRequest = { yearDropdownExpanded = false }
                    ) {
                        years.forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year.toString()) },
                                onClick = {
                                    selectedYear = year
                                    yearDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Выбор месяца
                ExposedDropdownMenuBox(
                    expanded = monthDropdownExpanded,
                    onExpandedChange = { monthDropdownExpanded = it }
                ) {
                    TextField(
                        value = selectedMonth.toString(),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { TrailingIcon(expanded = monthDropdownExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = monthDropdownExpanded,
                        onDismissRequest = { monthDropdownExpanded = false }
                    ) {
                        months.forEach { month ->
                            DropdownMenuItem(
                                text = { Text(month.toString()) },
                                onClick = {
                                    selectedMonth = month
                                    monthDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {/* onConfirm(selectedYear, selectedMonth)*/ }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}


@Composable
fun OperationItem(operation: Operation) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Круглая иконка
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer

        ) {
            Icon(
                imageVector = Icons.Default.Shop,
                contentDescription = null,
                modifier = Modifier.padding(8.dp),
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Описание операции (занимает доступное пространство)
        Text(
            text = operation.message,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        // Колонка с суммой и датой (выравнивание по правому краю)
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${operation.amount} ₽",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error

            )
            Text(
                text = "24.12.2026",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}