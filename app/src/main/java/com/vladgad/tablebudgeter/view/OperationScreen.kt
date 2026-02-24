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
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FoodBank
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
import androidx.compose.material.icons.filled.Title
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.launch


data class ChipElement(
    val image: ImageVector,
    val text: String,
)

val categories = listOf(
    ChipElement(Icons.Default.ShoppingCart, "Продукты"),
    ChipElement(Icons.Default.DirectionsCar, "Транспорт"),
    ChipElement(Icons.Default.LocalCafe, "Кафе"),
    ChipElement(Icons.Default.FitnessCenter, "Спорт"),
    ChipElement(Icons.Default.Movie, "Кино")
)


@Composable
fun OperationScreen() {
    TagMessageGeoRow()
    //PaymentRow()
    //CalculatorScreen()
    //CategoryGridSelector()
    //CustomTabsScreen()
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


@Composable
fun OperationTypeElement(icon: ImageVector, typeOperation: String) {
    var isSelected by remember { mutableStateOf(false) }

    val backgroundColor = if (isSelected) Color.Yellow else Color.LightGray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = { isSelected = true })
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

@Preview(showBackground = true)
@Composable
fun CategoryGridSelector() {
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

    var selectedIndex by remember { mutableIntStateOf(-1) } // -1 = ничего не выбрано

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
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun operationCreator() {

}

@Preview(showBackground = true)
@Composable
fun CalculatorScreen() {
    var amount by remember { mutableStateOf("0") }
    var note by remember { mutableStateOf("") }

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
                                    amount = amount.dropLast(1)
                                }
                            }

                            else -> {
                                // Обработка цифр и запятой
                                if (label.all { it.isDigit() }) {
                                    amount += label
                                } else if (label == "," && amount.isNotEmpty() && !amount.contains(",")) {
                                    amount += label
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


// Модель для счёта
data class Account(val icon: ImageVector, val name: String)

// Модель для приоритета
data class Priority(val symbol: String)

@Preview(showBackground = true)
@Composable
fun PaymentRow() {
    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var selectedPriority by remember { mutableStateOf<Priority?>(null) }
    var amount by remember { mutableStateOf("0") }

    val accounts = listOf(
        Account(Icons.Default.AccountBalance, "Т-Банк"),
        Account(Icons.Default.AccountBalanceWallet, "Сбер"),
        Account(Icons.Default.Business, "ВТБ"),
        Account(Icons.Default.Money, "Наличка")
    )

    val priorities = listOf(
        Priority("-2"), Priority("-1"), Priority("0"), Priority("+"), Priority("$")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Левая часть (два списка) занимает всё доступное место
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Список счетов (обычная Row, без LazyRow)
            accounts.forEach { account ->
                AccountChip(
                    account = account,
                    isSelected = selectedAccount == account,
                    onClick = { selectedAccount = account }
                )
                Spacer(modifier = Modifier.width(2.dp))
            }

            // Вертикальный разделитель между списками
            Divider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp),
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(2.dp))

            // Список приоритетов
            priorities.forEach { priority ->
                PriorityChip(
                    priority = priority,
                    isSelected = selectedPriority == priority,
                    onClick = { selectedPriority = priority }
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        // Текст с суммой справа (фиксированная ширина)
        Text(
            text = amount,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun AccountChip(
    account: Account,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (isSelected) 4.dp else 1.dp,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = account.icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = account.name,
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
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = if (isSelected) 4.dp else 1.dp,
        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TagMessageGeoRow() {
    var tagText by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }
    var isGeoChecked by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val tags = listOf("еда", "транспорт", "развлечения", "здоровье", "кафе")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp) // фиксированная высота для всех элементов
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Поле тега (30% ширины)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
        ) {
            OutlinedTextField(
                value = tagText,
                onValueChange = { tagText = it },
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
                        text = { Text(tag) },
                        onClick = {
                            tagText = tag
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Поле сообщения (50% ширины)
        OutlinedTextField(
            value = messageText,
            onValueChange = { messageText = it },
            placeholder = { Text("Сообщение") },
            leadingIcon = { Icon(Icons.Default.Message, contentDescription = null) },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight()
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Блок гео (20% ширины)
        Box(
            modifier = Modifier
                .weight(0.2f)
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
                    modifier = Modifier.size(20.dp)
                )
                Checkbox(
                    checked = isGeoChecked,
                    onCheckedChange = { isGeoChecked = it },
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}