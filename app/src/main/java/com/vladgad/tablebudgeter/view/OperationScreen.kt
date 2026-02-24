package com.vladgad.tablebudgeter.view


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vladgad.tablebudgeter.viewmodel.OperationData
import com.vladgad.tablebudgeter.viewmodel.OperationViewModel
import kotlinx.coroutines.launch


data class ChipElement(
    val image: ImageVector,
    val text: String,
)

// Модель для приоритета
data class Priority(val symbol: String)


@Composable
fun OperationScreen() {
    operationCreator()
    // TagMessageGeoRow()
    //PaymentRow()
    //CalculatorScreen()
    //CategoryGridSelector()
    //CustomTabsScreen()
}

//@Preview(showBackground = true)
@Composable
fun operationCreator() {
    val viewModel: OperationViewModel = viewModel()

    val operationData by viewModel.operationData.collectAsState()

    Text(text = "${operationData.amount}")
//    CategoryGridSelector(
//        selectedIndex = operationData.typeOperation,
//        onTypeOperationChange = { viewModel.updateTypeOperation(it) }
//    )
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
        onAmountChange = { viewModel.updateAmount(it) })
}


@Composable
fun OperationTypeElement(
    icon: ImageVector, typeOperation: String, isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
) {
    val backgroundColor = if (isSelected) Color.Yellow else Color.LightGray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = { onSelectedChange(!isSelected) })
            .padding(8.dp)
    ) {

        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, "")
        }

        Text(
            text = typeOperation,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun CategoryGridSelector(
    selectedIndex: Int,
    onTypeOperationChange: (Int) -> Unit
) {
    // Список категорий: иконка, название
    val categories = listOf(
        ChipElement(Icons.Default.ShoppingCart, "Продукты"),
        ChipElement(Icons.Default.DirectionsCar, "Транспорт"),
        ChipElement(Icons.Default.LocalCafe, "Кафе"),
        ChipElement(Icons.Default.FitnessCenter, "Спорт"),
        ChipElement(Icons.Default.Movie, "Кино"),
        ChipElement(Icons.Default.Book, "Книги"),
        ChipElement(Icons.Default.Phone, "Связь"),
        ChipElement(Icons.Default.Home, "Жильё"),
        ChipElement(Icons.Default.Pets, "Зоотовары"),
        ChipElement(Icons.Default.HealthAndSafety, "Здоровье")
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(4), // ровно 4 столбца
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(categories) { index, (icon, label) ->
            OperationTypeElement(
                icon = icon,
                typeOperation = label,
                isSelected = selectedIndex == index,
                onSelectedChange = { isSelected ->
                    // Если выбрали этот элемент, передаём его индекс, иначе -1
                    onTypeOperationChange(if (isSelected) index else -1)
                }
            )
        }
    }
}

@Composable
fun PaymentRow(
    selectedAccount: Int,
    selectedPriority: Int,
    amount: String,
    onSelectedAccountChange: (Int) -> Unit,
    onSelectedPriorityChange: (Int) -> Unit
) {
    val accounts = listOf(
        ChipElement(Icons.Default.AccountBalance, "Т-Банк"),
        ChipElement(Icons.Default.AccountBalanceWallet, "Сбер"),
        ChipElement(Icons.Default.Business, "ВТБ"),
        ChipElement(Icons.Default.Money, "Наличка"),
    )
    val priorities = listOf(
        Priority("-2"), Priority("-1"), Priority("0"), Priority("+"), Priority("$")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Левая часть с горизонтальной прокруткой
        Row(
            modifier = Modifier
                .weight(4f)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Список счетов
            accounts.forEachIndexed { index, account ->
                AccountChip(
                    account = account,
                    isSelected = selectedAccount == index,
                    onSelectedChange = { isSelected ->
                        // Если выбрали этот элемент, передаём его индекс, иначе -1
                        onSelectedAccountChange(if (isSelected) index else -1)
                    }
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            // Вертикальный разделитель
            Divider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp),
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(4.dp))

            // Список приоритетов
            priorities.forEachIndexed { index, priority ->
                PriorityChip(
                    priority = priority,
                    isSelected = selectedPriority == index,
                    onSelectedChange = { isSelected ->
                        // Если выбрали этот элемент, передаём его индекс, иначе -1
                        onSelectedPriorityChange(if (isSelected) index else -1)
                    }
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        // Текст суммы справа
        Text(
            text = amount,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        )
    }
}


@Composable
fun AccountChip(
    account: ChipElement,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (isSelected) 4.dp else 1.dp,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelectedChange(!isSelected) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = account.image,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = account.text,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 8.sp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PriorityChip(
    priority: Priority,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = if (isSelected) 4.dp else 1.dp,
        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelectedChange(!isSelected) }
    ) {
        Text(
            text = priority.symbol,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
fun CalculatorScreen(amount: String, onAmountChange: (String) -> Unit) {
    val buttons = listOf(
        "7", "8", "9", "📅",
        "4", "5", "6", "+",
        "1", "2", "3", "-",
        ",", "0", "✖", "✓"
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {

        // Сетка кнопок 4x4
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(buttons) { label ->
                CalculatorButton(
                    label = label,
                    onClick = {
                        // Здесь можно добавить логику обработки нажатий
                        when (label) {
                            "✓" -> { /* подтвердить */
                            }

                            "✖" -> {
                                if (amount.isNotEmpty()) {
                                    onAmountChange(amount.dropLast(1))
                                }
                            }

                            else -> {
                                // Обработка цифр и запятой
                                if (label.all { it.isDigit() }) {
                                    if (amount.length == 1 && amount == "0")
                                        onAmountChange(label)
                                    else
                                        onAmountChange(amount + label)
                                } else if (label == "," && amount.isNotEmpty() && !amount.contains(",")) {
                                    onAmountChange(amount + label)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.5f), // прямоугольные кнопки
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagMessageGeoRow(
    tagText: String, messageText: String, isGeoChecked: Boolean,
    onTagChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onGeoCheckedChange: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val tags = listOf("еда", "транспорт", "развлечения", "здоровье", "кафе")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp) // фиксированная высота для всех элементов
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Поле тега (30% ширины)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
        ) {
            OutlinedTextField(
                value = tagText,
                onValueChange = { onTagChange(it) },
                readOnly = false,
                placeholder = { Text("Тэг") },
                leadingIcon = { Icon(Icons.Default.Label, contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxSize()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                tags.forEach { tag ->
                    DropdownMenuItem(
                        text = { Text(tag, fontSize = 6.sp) },
                        onClick = {
                            onTagChange(tag)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Поле сообщения (50% ширины)
        OutlinedTextField(
            value = messageText,
            onValueChange = { onMessageChange(it) },
            placeholder = { Text("Сообщение") },
            leadingIcon = { Icon(Icons.Default.Message, contentDescription = null) },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight()
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Блок гео (20% ширины)
        Box(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(horizontal = 1.dp)
                )
                Checkbox(
                    checked = isGeoChecked,
                    onCheckedChange = { onGeoCheckedChange(it) },
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTabsScreen() {
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
                0 -> ExpensesContent()
                1 -> IncomesContent()
                2 -> TransfersContent()
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

// Заглушки контента (замените на свои)
@Composable
fun ExpensesContent() = Box(
    Modifier
        .fillMaxSize()
        .padding(16.dp)
) { Text("Список расходов") }

@Composable
fun IncomesContent() = Box(
    Modifier
        .fillMaxSize()
        .padding(16.dp)
) { Text("Список доходов") }

@Composable
fun TransfersContent() = Box(
    Modifier
        .fillMaxSize()
        .padding(16.dp)
) { Text("Список переводов") }


