package com.vladgad.tablebudgeter.model.table

import com.google.gson.annotations.SerializedName

// Data классы для ответа от Google Sheets API
data class ValueRange(
    @SerializedName("range")
    val range: String,

    @SerializedName("majorDimension")
    val majorDimension: String, // "ROWS" или "COLUMNS"

    @SerializedName("values")
    val values: List<List<Any>>?
)


data class CellData(
    @SerializedName("userEnteredValue") val userEnteredValue: ExtendedValue
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
    val sheetId: Long? = null,
    val startRowIndex: Int? = null,
    val endRowIndex: Int? = null,
    val startColumnIndex: Int? = null,
    val endColumnIndex: Int? = null,
    val dimension: String? = null,
    val startIndex: Int? = null,
    val endIndex: Int? = null,
)

data class UpdateCellsRequest(
    val range: GridRange, val fields: String, val rows: List<RowData>
)

data class InsertDimensionRequest(
    val range: GridRange,
    val inheritFromBefore: Boolean,
)

sealed class SheetRequest {
    data class UpdateCells(
        val updateCells: UpdateCellsRequest
    ) : SheetRequest()

    data class InsertDimension(
        val insertDimension: InsertDimensionRequest
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
            values = headers.map { createTextCell(it) })
    }

    // Создаем диапазон
    fun createRange(
        sheetId: Long, startRow: Int, endRow: Int, startColumn: Int, endColumn: Int
    ): GridRange {
        return GridRange(
            sheetId = sheetId,
            startRowIndex = startRow,
            endRowIndex = endRow,
            startColumnIndex = startColumn,
            endColumnIndex = endColumn
        )
    }

    fun createRange(
        sheetId: Long,
        dimension: String,
        startIndex: Int,
        endIndex: Int,
    ): GridRange {
        return GridRange(
            sheetId = sheetId,
            dimension = dimension,
            startIndex = startIndex,
            endIndex = endIndex,
        )
    }

    // Создаем запрос на обновление заголовков
    fun createHeadersRequest(
        sheetId: Long, rowIndex: Int, columnIndex: Int, headers: List<String>
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
                        ), fields = "userEnteredValue", rows = listOf(createRow(headers))
                    )
                )
            )
        )
    }

    fun createInsertEmptyRowRequest(
        sheetId: Long, rowIndex: Int,
    ): BatchUpdateRequest {
        return BatchUpdateRequest(
            requests = listOf(
                SheetRequest.InsertDimension(
                    insertDimension = InsertDimensionRequest(
                        range = createRange(
                            sheetId = sheetId,
                            dimension = "ROWS",
                            startIndex = rowIndex,
                            endIndex = rowIndex + 1,
                        ),
                        inheritFromBefore = false,
                    )
                )
            )
        )
    }

    fun createInsertEmptyRowsRequest(
        sheetId: Long, rowIndex: Int, numRows: Int,
    ): BatchUpdateRequest {
        return BatchUpdateRequest(
            requests = listOf(
                SheetRequest.InsertDimension(
                    insertDimension = InsertDimensionRequest(
                        range = createRange(
                            sheetId = sheetId,
                            dimension = "ROWS",
                            startIndex = rowIndex,
                            endIndex = rowIndex + numRows,
                        ),
                        inheritFromBefore = false,
                    )
                )
            )
        )
    }


    // Создаем запрос на обновление нескольких строк
    fun createUpdateRowsRequest(
        sheetId: Long,
        startRowIndex: Int,
        dataRows: List<List<Any>>
    ): BatchUpdateRequest {
        val numRows = dataRows.size
        val numColumns = dataRows.maxOfOrNull { it.size } ?: 0

        val rows = dataRows.map { rowValues ->
            RowData(
                values = rowValues.map { value ->
                    CellData(
                        userEnteredValue = when (value) {
                            is String -> ExtendedValue(stringValue = value)
                            is Int -> ExtendedValue(numberValue = value.toDouble())
                            is Double -> ExtendedValue(numberValue = value)
                            is Float -> ExtendedValue(numberValue = value.toDouble())
                            is Boolean -> ExtendedValue(boolValue = value)
                            else -> ExtendedValue(stringValue = value.toString())
                        }
                    )
                }
            )
        }

        return BatchUpdateRequest(
            requests = listOf(
                SheetRequest.UpdateCells(
                    updateCells = UpdateCellsRequest(
                        range = GridRange(
                            sheetId = sheetId,
                            startRowIndex = startRowIndex,
                            endRowIndex = startRowIndex + numRows,
                            startColumnIndex = 0,
                            endColumnIndex = numColumns
                        ),
                        fields = "userEnteredValue",
                        rows = rows
                    )
                )
            )
        )
    }

    // Создаем комбинированный запрос (вставка + обновление)
    fun createInsertAndUpdateRowsRequest(
        sheetId: Long,
        startRowIndex: Int,
        dataRows: List<List<Any>>
    ): BatchUpdateRequest {
        val numRows = dataRows.size
        val numColumns = dataRows.maxOfOrNull { it.size } ?: 0

        return BatchUpdateRequest(
            requests = listOf(
                // 1. Вставляем пустые строки
                SheetRequest.InsertDimension(
                    insertDimension = InsertDimensionRequest(
                        range = GridRange(
                            sheetId = sheetId,
                            dimension = "ROWS",
                            startIndex = startRowIndex,
                            endIndex = startRowIndex + numRows
                        ),
                        inheritFromBefore = false
                    )
                ),
                // 2. Записываем данные
                SheetRequest.UpdateCells(
                    updateCells = UpdateCellsRequest(
                        range = GridRange(
                            sheetId = sheetId,
                            startRowIndex = startRowIndex,
                            endRowIndex = startRowIndex + numRows,
                            startColumnIndex = 0,
                            endColumnIndex = numColumns
                        ),
                        fields = "userEnteredValue",
                        rows = dataRows.map { rowValues ->
                            RowData(
                                values = rowValues.map { value ->
                                    CellData(
                                        userEnteredValue = when (value) {
                                            is String -> ExtendedValue(stringValue = value)
                                            is Int -> ExtendedValue(numberValue = value.toDouble())
                                            is Double -> ExtendedValue(numberValue = value)
                                            is Float -> ExtendedValue(numberValue = value.toDouble())
                                            is Boolean -> ExtendedValue(boolValue = value)
                                            else -> ExtendedValue(stringValue = value.toString())
                                        }
                                    )
                                }
                            )
                        }
                    )
                )
            )
        )
    }

    // Data класс для запроса обновления
    data class UpdateRowRequest(
        @SerializedName("range")
        val range: String,

        @SerializedName("values")
        val values: List<List<Any>>,

        @SerializedName("majorDimension")
        val majorDimension: String = "ROWS"
    )


}