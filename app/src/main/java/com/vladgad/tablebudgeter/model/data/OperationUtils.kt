package com.vladgad.tablebudgeter.model.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun parseListOperation(rowsResult: List<List<String>>): MutableList<Operation> {

    val operations = mutableListOf<Operation>()
    val dateFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()),
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()),
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()),
        SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())
    )

    // Пропускаем заголовок (если есть) и преобразуем строки
    val dataRows = if (rowsResult.isNotEmpty() &&
        rowsResult[0].any { it.contains("Тип") || it.contains("Действие") }
    ) {
        rowsResult.drop(1) // Пропускаем строку заголовков
    } else {
        rowsResult
    }

    for ((index, row) in dataRows.withIndex()) {
        try {
            // Проверяем, что в строке достаточно данных
            if (row.size >= 8) {
                val operation = parseOperationRow(row, dateFormats)
                operations.add(operation)
            } else if (row.size >= 4) {
                // Минимальные данные: тип, дата, сумма, счет
                val operation = Operation(
                    typeOperation = row[0].toString(),
                    dateOperation = parseDate(row[1].toString(), dateFormats),
                    amount = row[2].toString().toDoubleOrNull() ?: 0.0,
                    account = row[3].toString(),
                    tag = if (row.size > 4) row[4].toString() else "",
                    priority = if (row.size > 5) row[5].toString().toIntOrNull() ?: 3 else 3,
                    place = if (row.size > 6) row[6].toString() else "",
                    message = if (row.size > 7) row[7].toString() else ""
                )
                operations.add(operation)
            }
        } catch (e: Exception) {
            println("Ошибка при парсинге строки $index: ${e.message}. Строка: $row")
            // Продолжаем обработку остальных строк
        }
    }

    return operations

}

private fun parseOperationRow(row: List<String>, dateFormats: List<SimpleDateFormat>): Operation {
    return Operation(
        typeOperation = row.getOrNull(0)?.toString() ?: "",
        dateOperation = parseDate(row.getOrNull(1)?.toString() ?: "", dateFormats),
        amount = row.getOrNull(2)?.toString()?.toDoubleOrNull() ?: 0.0,
        account = row.getOrNull(3)?.toString() ?: "",
        id = row.getOrNull(4)?.toString()?.toLongOrNull() ?: Date().time,
        tag = row.getOrNull(5)?.toString() ?: "",
        priority = row.getOrNull(6)?.toString()?.toIntOrNull() ?: 3,
        place = row.getOrNull(7)?.toString() ?: "",
        message = row.getOrNull(8)?.toString() ?: ""
    )
}

private fun parseDate(dateString: String, dateFormats: List<SimpleDateFormat>): Long {
    if (dateString.isEmpty()) return Date().time

    // Пробуем распарсить как timestamp
    dateString.toLongOrNull()?.let { return it }

    // Пробуем разные форматы даты
    for (format in dateFormats) {
        try {
            return format.parse(dateString)?.time ?: continue
        } catch (e: Exception) {
            continue
        }
    }

    // Если не удалось распарсить, возвращаем текущее время
    return Date().time
}