package com.vladgad.tablebudgeter.model.table

import com.google.gson.annotations.SerializedName

data class CellData(
    @SerializedName("userEnteredValue")
    val userEnteredValue: ExtendedValue
)
data class ExtendedValue(
    val stringValue: String? = null,
    val numberValue: Double? = null,
    val boolValue: Boolean? = null,
    val formulaValue: String? = null
    // Можно добавить другие типы значений при необходимости
)

data class RowData(
    val values: List<CellData>
)

data class GridRange(
    val sheetId: Long,
    val startRowIndex: Int,
    val endRowIndex: Int,
    val startColumnIndex: Int,
    val endColumnIndex: Int
)

data class UpdateCellsRequest(
    val range: GridRange,
    val fields: String,
    val rows: List<RowData>
)

sealed class SheetRequest {
    data class UpdateCells(
        val updateCells: UpdateCellsRequest
    ) : SheetRequest()

    // Можно расширять другими типами запросов
}


data class BatchUpdateRequest(
    val requests: List<SheetRequest>
)

// Утилиты для удобного создания
object SheetRequestBuilder {

    // Создаем ячейку с текстом
    fun createTextCell(text: String): CellData {
        return CellData(
            userEnteredValue = ExtendedValue(stringValue = text)
        )
    }

    // Создаем ячейку с числом
    fun createNumberCell(number: Double): CellData {
        return CellData(
            userEnteredValue = ExtendedValue(numberValue = number)
        )
    }

    // Создаем строку из текстовых ячеек
    fun createRow(headers: List<String>): RowData {
        return RowData(
            values = headers.map { createTextCell(it) }
        )
    }

    // Создаем диапазон
    fun createRange(
        sheetId: Long,
        startRow: Int,
        endRow: Int,
        startColumn: Int,
        endColumn: Int
    ): GridRange {
        return GridRange(
            sheetId = sheetId,
            startRowIndex = startRow,
            endRowIndex = endRow,
            startColumnIndex = startColumn,
            endColumnIndex = endColumn
        )
    }

    // Создаем запрос на обновление заголовков
    fun createHeadersRequest(
        sheetId: Long,
        rowIndex: Int,
        columnIndex: Int,
        headers: List<String>
    ): BatchUpdateRequest {
        return BatchUpdateRequest(
            requests = listOf(
                SheetRequest.UpdateCells(
                    updateCells = UpdateCellsRequest(
                        range = createRange(
                            sheetId = sheetId,
                            startRow = rowIndex,
                            endRow = rowIndex + 1,
                            startColumn = columnIndex,
                            endColumn = columnIndex + headers.size
                        ),
                        fields = "userEnteredValue",
                        rows = listOf(createRow(headers))
                    )
                )
            )
        )
    }
}