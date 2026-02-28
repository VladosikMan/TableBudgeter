package com.vladgad.tablebudgeter.view.operation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vladgad.tablebudgeter.view.data.ChipElement
import com.vladgad.tablebudgeter.view.data.Priority

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

