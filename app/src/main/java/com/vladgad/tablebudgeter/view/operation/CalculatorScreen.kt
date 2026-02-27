package com.vladgad.tablebudgeter.view.operation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun CalculatorScreen(
    amount: String,
    onAmountChange: (String) -> Unit,
    createOperation: () -> Unit
) {
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
                            "✓" -> {
                                createOperation()
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

