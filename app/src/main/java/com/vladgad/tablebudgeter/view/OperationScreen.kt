package com.vladgad.tablebudgeter.view


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vladgad.tablebudgeter.view.data.ChipElement
import com.vladgad.tablebudgeter.view.operation.CategoryGridSelector
import com.vladgad.tablebudgeter.view.operation.OperationBottom
import com.vladgad.tablebudgeter.viewmodel.OperationViewModel
import kotlinx.coroutines.launch


const val  EXPENCE_MODIF : Byte = -1
const val  INCOME_MODIF : Byte = 1
@Composable
fun OperationScreen(viewModel: OperationViewModel = viewModel()) {
    OperationElement(viewModel, viewModel.incomeCategories, INCOME_MODIF)
}

@Composable
fun OperationElement(viewModel: OperationViewModel , categories: List<ChipElement>, modif : Byte){
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


