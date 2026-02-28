package com.vladgad.tablebudgeter.view

import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.vladgad.tablebudgeter.view.data.ChipElement
import com.vladgad.tablebudgeter.view.operation.CategoryGridSelector
import com.vladgad.tablebudgeter.view.operation.OperationBottom
import com.vladgad.tablebudgeter.viewmodel.OperationViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.viewmodel.AnalyticsViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random

private const val mTag = "AnalyticsScreen"
@Composable
fun AnalyticsScreen() {
    val viewModel : AnalyticsViewModel = viewModel()
    val operations by viewModel.operations.collectAsState()

    AnalyticsScreen2(operations)
}



@Composable
fun AnalyticsScreen2(operations: List<Operation>) {
    // Вычисляем статистику
    val totalIncome = operations.filter { it.amount > 0 }.sumOf { it.amount }
    val totalExpense = operations.filter { it.amount < 0 }.sumOf { -1 * it.amount }
    val total = totalIncome + totalExpense
    val incomeAngle = if (total > 0) (totalIncome / total * 360f).toFloat() else 0f
    val expenseAngle = if (total > 0) (totalExpense / total * 360f).toFloat() else 0f
    val maxIncome = operations.filter {  it.amount > 0 }.maxByOrNull { it.amount }
    val maxExpense = operations.filter {  it.amount < 0 }.minByOrNull { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Аналитика", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        item {
            // Карточка с диаграммой
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .drawBehind {
                                drawPieChart(incomeAngle, expenseAngle)
                            }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LegendItem(color = Color.Blue, text = "Доходы: ${formatAmount(totalIncome)}")
                        LegendItem(color = Color.Red, text = "Расходы: ${formatAmount(totalExpense)}")
                        LegendItem(color = Color.Yellow, text = "Бюджет: ${formatAmount(totalIncome - totalExpense)}")
                    }
                }
            }
        }

        item {
            // Карточка с максимумами
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Максимальный доход", style = MaterialTheme.typography.titleMedium)
                    if (maxIncome != null) {
                        Text("${maxIncome.typeOperation}: ${formatAmount(maxIncome.amount)} (${maxIncome.message})")
                    } else {
                        Text("Нет доходов")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Максимальный расход", style = MaterialTheme.typography.titleMedium)
                    if (maxExpense != null) {
                        Text("${maxExpense.typeOperation}: ${formatAmount(maxExpense.amount)} (${maxExpense.message})")
                    } else {
                        Text("Нет расходов")
                    }
                }
            }
        }

        item {
            Text("Последние операции", style = MaterialTheme.typography.titleLarge)
        }

        items(operations.take(5)) { operation ->
            OperationItem(operation)
        }
    }
}

// Рисование круговой диаграммы
fun DrawScope.drawPieChart(incomeAngle: Float, expenseAngle: Float) {
    val radius = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)
    val startAngle = -90f

    if (incomeAngle > 0) {
        drawArc(
            color = Color.Green,
            startAngle = startAngle,
            sweepAngle = incomeAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
    }
    if (expenseAngle > 0) {
        drawArc(
            color = Color.Red,
            startAngle = startAngle + incomeAngle,
            sweepAngle = expenseAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
    }
    if (incomeAngle == 0f && expenseAngle == 0f) {
        drawCircle(color = Color.Gray, radius = radius, center = center)
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .drawBehind { drawCircle(color) }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

fun formatAmount(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    return formatter.format(amount)
}
