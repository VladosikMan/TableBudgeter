package com.vladgad.tablebudgeter.model.table


import com.google.gson.JsonObject
import com.vladgad.tablebudgeter.http.KtorClient.Companion.INSTANCE_HTTP_CLIENT
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.parseListOperation
import com.vladgad.tablebudgeter.model.table.SheetRequestBuilder.createHeadersRequest
import com.vladgad.tablebudgeter.model.table.SheetRequestBuilder.createInsertAndUpdateRowsRequest
import com.vladgad.tablebudgeter.model.table.SheetRequestBuilder.createInsertEmptyRowRequest
import com.vladgad.tablebudgeter.utils.GsonClient.Companion.INSTANCE_GSON
import com.vladgad.tablebudgeter.utils.Utils.Companion.formatDate
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SheetsServiceHelper() {
    companion object {
        val INSTANCE_SHEET_HELPER: SheetsServiceHelper by lazy {
            SheetsServiceHelper()
        }
    }

    private var accessToken: String? = null
    private val headers =
        listOf("Действие", "Дата", "Сумма", "Счёт", "Приоритет", "Тэг", "Место", "Сообщение", "Id")

    private val client = INSTANCE_HTTP_CLIENT
    private val gson = INSTANCE_GSON

    fun updateAccessToken(newToken: String?) {
        this.accessToken = newToken
    }

    // 1. СОЗДАТЬ НОВУЮ ТАБЛИЦУ
    suspend fun createSpreadsheet(title: String): String? = withContext(Dispatchers.IO) {
        val token = accessToken ?: return@withContext null

        val url = "https://sheets.googleapis.com/v4/spreadsheets"

        // Тело запроса в формате JSON
        val requestBody = """
        {
          "properties": {
            "title": "$title"
          },
          "sheets": [
            {
              "properties": {
                "title": "Sheet1",
                "gridProperties": {
                  "rowCount": 100,
                  "columnCount": 10
                }
              }
            }
          ]
        }
        """.trimIndent()

        return@withContext try {
            val response: String = client.post(url) {
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody(requestBody)
            }.body()

            // Парсим ответ и извлекаем spreadsheetId
            val jsonResponse = gson.fromJson(response, JsonObject::class.java)
            jsonResponse.get("spreadsheetId")?.asString
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun writeHeaderRowBySheetId(
        spreadsheetId: String,
        sheetId: Long,
    ): Boolean = withContext(Dispatchers.IO) {
        val token = accessToken ?: return@withContext false
        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId:batchUpdate"
        val requestBody = gson.toJson(
            createHeadersRequest(
                sheetId = sheetId,
                rowIndex = 0,
                columnIndex = 0,
                headers = headers
            )
        )
        return@withContext try {
            val response: HttpResponse = client.post(url) {
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                setBody(requestBody)
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // 2. ПРОЧИТАТЬ ДАННЫЕ
    suspend fun readData(spreadsheetId: String, range: String): List<List<String>>? =
        withContext(Dispatchers.IO) {
            val token = accessToken ?: return@withContext null

            // Кодируем range для URL
            val encodedRange = range.encodeURLParameter()
            val url =
                "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId/values/$encodedRange"

            return@withContext try {
                val response: String = client.get(url) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }.body()

                val jsonResponse = gson.fromJson(response, JsonObject::class.java)

                // Извлекаем данные из JSON ответа
                val valuesArray = jsonResponse.getAsJsonArray("values")
                if (valuesArray == null) {
                    emptyList()
                } else {
                    val result = mutableListOf<List<String>>()
                    for (rowElement in valuesArray) {
                        val rowArray = rowElement.asJsonArray
                        val rowList = mutableListOf<String>()
                        for (cellElement in rowArray) {
                            rowList.add(cellElement.asString)
                        }
                        result.add(rowList)
                    }
                    result
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    // 5. ДОБАВИТЬ НОВЫЙ ЛИСТ
    suspend fun addNewSheet(spreadsheetId: String, sheetTitle: String): Boolean =
        withContext(Dispatchers.IO) {
            val token = accessToken ?: return@withContext false

            val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId:batchUpdate"

            val requestBody = """
        {
          "requests": [
            {
              "addSheet": {
                "properties": {
                  "title": "$sheetTitle"
                }
              }
            }
          ]
        }
        """.trimIndent()

            return@withContext try {
                val response: HttpResponse = client.post(url) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                    setBody(requestBody)
                }

                response.status.isSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    // Получить список таблиц (ID → Название)
    suspend fun getSpreadsheetsList(pageSize: Int = 100): String = withContext(Dispatchers.IO) {
        val token = accessToken ?: return@withContext "пись-пись"
        val url = "https://www.googleapis.com/drive/v3/files"

        try {
            val response: String = client.get(url) {
                header("Authorization", "Bearer $token")
                // Ключевые параметры для поиска во всех дисках
                parameter("supportsAllDrives", "true")
                parameter("includeItemsFromAllDrives", "true")
                // Параметры запроса
                parameter("pageSize", pageSize.toString())
                parameter(
                    "q",
                    "mimeType='application/vnd.google-apps.spreadsheet' and trashed = false"
                )
                parameter(
                    "fields",
                    "files(id, name, mimeType, createdTime, modifiedTime, webViewLink)"
                )
                parameter("orderBy", "modifiedTime desc")
            }.body()
            return@withContext response
        } catch (e: Exception) {
            e.printStackTrace()
            //            emptyList()
        }.toString()
    }

    // 4. ПОЛУЧИТЬ ИНФОРМАЦИЮ О ТАБЛИЦЕ
    suspend fun getSpreadsheetInfo(spreadsheetId: String): JsonObject? =
        withContext(Dispatchers.IO) {
            val token = accessToken ?: return@withContext null

            val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId"

            return@withContext try {
                val response: String = client.get(url) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }.body()

                gson.fromJson(response, JsonObject::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    suspend fun insertEmptyRow(
        spreadsheetId: String,
        sheetId: Long,
        rowIndex: Int
    ): Boolean = withContext(Dispatchers.IO) {
        val token = accessToken ?: return@withContext false
        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId:batchUpdate"
        val requestBody1 = gson.toJson(
            createInsertEmptyRowRequest(
                sheetId = sheetId,
                rowIndex = rowIndex,
            )
        )
        return@withContext try {
            val response: HttpResponse = client.post(url) {
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                setBody(requestBody1)
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun addDataRows(
        spreadsheetId: String,
        sheetId: Long, // Используем sheetId
        startRowIndex: Int,
        operations: List<Operation>
    ): Boolean = withContext(Dispatchers.IO) {
        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId:batchUpdate"
        val token = accessToken ?: return@withContext false
        // 1. Преобразуем операции в строки таблицы
        val dataRows = operations.map { operation ->
            listOf<Any>(
                operation.typeOperation,
                formatDate(operation.dateOperation),
                operation.amount,
                operation.account,
                operation.tag,
                operation.priority,
                operation.place,
                operation.message,
                operation.id
                // Можно добавить operation.id если нужно
            )
        }
        val requestBody = gson.toJson(
            createInsertAndUpdateRowsRequest(
                sheetId = sheetId,
                startRowIndex = startRowIndex,
                dataRows = dataRows
            )
        )

        return@withContext try {
            val response: HttpResponse = client.post(url) {
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                setBody(requestBody)
            }
            if (response.status.isSuccess())
                return@withContext true
            else
                return@withContext false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getAllRows(
        spreadsheetId: String,
        sheetId: Long,
        includeEmptyRows: Boolean = false
    ): List<Operation> = withContext(Dispatchers.IO) {
        val token = accessToken ?: throw IllegalStateException("No access token")
        // 1. Определяем название листа (sheet name)
        val targetSheetName = getSheetNameById(spreadsheetId, sheetId, token)
        // 2. Формируем URL для запроса
        val range = escapeSheetName(targetSheetName) // Экранируем название листа
        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId/values/$range"
        // 3. Выполняем запрос
        val response = client.get(url) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }
        if (!response.status.isSuccess()) {
            val errorBody = response.bodyAsText()
            throw RuntimeException("Failed to get rows: ${response.status}. Error: $errorBody")
        }
        val json = gson.fromJson(response.bodyAsText(), ValueRange::class.java)
        // 5. Преобразуем данные
        val allRows = json.values?.map { row ->
            row.map { cell ->
                when (cell) {
                    is String -> cell
                    is Number -> cell.toString()
                    is Boolean -> if (cell) "TRUE" else "FALSE"
                    else -> cell?.toString() ?: ""
                }
            }
        } ?: emptyList()

        // 6. Фильтруем пустые строки, если нужно
        if (includeEmptyRows) {
            allRows
        } else {
            allRows.filter { row ->
                row.any { cell -> cell.isNotBlank() }
            }
        }

        val list = parseListOperation(allRows)

        return@withContext list
    }

    suspend fun getNumRowById(
        spreadsheetId: String,
        sheetName: String,
        id: Long,
        idColumnIndex: Int = 8 // В какой колонке искать ID (по умолчанию A, индекс 0)
    ): Int = withContext(Dispatchers.IO) {

        val token = accessToken ?: throw IllegalStateException("No access token")
        // 1. Получаем все ID из указанной колонки
        val idRange =
            "${sheetName}!${getColumnLetter(idColumnIndex + 1)}:${getColumnLetter(idColumnIndex + 1)}"
        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId/values/$idRange"

        val response = client.get(url) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }

        if (!response.status.isSuccess()) {
            throw RuntimeException("Failed to get IDs: ${response.status}")
        }
        val valueRange = gson.fromJson(response.bodyAsText(), ValueRange::class.java)
        val idColumn = valueRange.values?.flatten() ?: emptyList()
        // 2. Ищем строку с нужным ID
        val rowIndex = findRowIndexById(idColumn, id)
        return@withContext rowIndex
    }

    suspend fun getRowById(
        spreadsheetId: String,
        sheetId: Long,
        id: Long,
    ): Operation = withContext(Dispatchers.IO) {
        val token = accessToken ?: throw IllegalStateException("No access token")
        val sheetName = getSheetNameById(spreadsheetId, sheetId, token)
        val rowIndex = getNumRowById(spreadsheetId, sheetName, id)
        val row = getRowByIndex(spreadsheetId, sheetName, rowIndex, token)
        val list = parseListOperation(listOf(row))
        return@withContext list[0]
    }


    // Получение строки по индексу
    private suspend fun getRowByIndex(
        spreadsheetId: String,
        sheetName: String,
        rowIndex: Int, // 0-based индекс строки
        token: String
    ): List<String> {
        // Получаем достаточно большое количество колонок (например, до Z)
        val columnCount = 26 // A-Z
        val startColumn = "A"
        val endColumn = getColumnLetter(columnCount)

        // Диапазон: одна строка, все колонки
        val range = "${sheetName}!${startColumn}${rowIndex + 1}:${endColumn}${rowIndex + 1}"
        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId/values/$range"

        val response = client.get(url) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }

        if (!response.status.isSuccess()) {
            throw RuntimeException("Failed to get row: ${response.status}")
        }

        val valueRange = gson.fromJson(response.bodyAsText(), ValueRange::class.java)
        return valueRange.values?.firstOrNull()?.map { it.toString() } ?: emptyList()
    }


    suspend fun updateRowById(
        spreadsheetId: String,
        sheetId: Long,
        id: Long, operation: Operation,
        startColumn: Int = 0 // С какой колонки начинать обновление
    ): Boolean = withContext(Dispatchers.IO) {
        val token = accessToken ?: throw IllegalStateException("No access token")
        val sheetName = getSheetNameById(spreadsheetId, sheetId, token)
        val rowIndex = getNumRowById(spreadsheetId, sheetName, id)
        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId:batchUpdate"
        // 2. Создаем значения строки
        val rowValues = listOf(
            operation.typeOperation,
            formatDate(operation.dateOperation),
            operation.amount,
            operation.account,
            operation.priority,
            operation.tag,
            operation.place,
            operation.message,
            operation.id
        )
        // 3. Преобразуем значения в RowData
        val rowData = RowData(
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
        val request = BatchUpdateRequest(
            requests = listOf(
                SheetRequest.UpdateCells(
                    updateCells = UpdateCellsRequest(
                        range = GridRange(
                            sheetId = sheetId,
                            startRowIndex = rowIndex,
                            endRowIndex = rowIndex + 1,
                            startColumnIndex = startColumn,
                            endColumnIndex = startColumn + rowValues.size
                        ),
                        fields = "userEnteredValue",
                        rows = listOf(rowData)
                    )
                )
            )
        )
        return@withContext try {
            val response: HttpResponse = client.post(url) {
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                setBody(gson.toJson(request))
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }


    }

    suspend fun deleteRowById(
        spreadsheetId: String,
        sheetId: Long,
        id: Long,
        startColumn: Int = 0 // С какой колонки начинать обновление
    ): Boolean = withContext(Dispatchers.IO) {

        val token = accessToken ?: return@withContext false
        val sheetName = getSheetNameById(spreadsheetId, sheetId, token)
        val rowIndex = getNumRowById(spreadsheetId, sheetName, id)
        // 1. Создаем запрос на удаление строки
        val request = BatchUpdateRequest(
            requests = listOf(
                SheetRequest.DeleteDimension(
                    deleteDimension = DeleteDimensionRequest(
                        range = GridRange(
                            sheetId = sheetId,
                            dimension = "ROWS",
                            startIndex = rowIndex,
                            endIndex = rowIndex + 1 // Удаляем одну строку
                        )
                    )
                )
            )
        )

        // 2. Отправляем запрос
        return@withContext try {
            val response =
                client.post("https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId:batchUpdate") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(gson.toJson(request))
                }
            response.status.isSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    suspend fun close() {
        client.close()
    }


    // Вспомогательная функция для получения названия листа по ID
    private suspend fun getSheetNameById(
        spreadsheetId: String,
        sheetId: Long,
        token: String
    ): String {
        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId"

        val response = client.get(url) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }

        if (!response.status.isSuccess()) {
            throw RuntimeException("Failed to get spreadsheet info")
        }

        val jsonString = response.bodyAsText()
        val spreadsheet = gson.fromJson(jsonString, Map::class.java)
        val sheets = spreadsheet["sheets"] as? List<Map<String, Any>>
            ?: throw RuntimeException("No sheets found")

        val sheet = sheets.find { sheetData ->
            val properties = sheetData["properties"] as? Map<String, Any>
            val sheetIdValue = properties?.get("sheetId") as? Number
            sheetIdValue?.toLong() == sheetId
        }

        val properties = sheet?.get("properties") as? Map<String, Any>
        val title = properties?.get("title") as? String

        return title ?: throw RuntimeException("Sheet with ID $sheetId not found")
    }

    // Экранирование названия листа (нужно для листов с пробелами и спецсимволами)
    private fun escapeSheetName(sheetName: String): String {
        // Если название содержит спецсимволы, заключаем в одинарные кавычки
        val needsQuotes = sheetName.any {
            !it.isLetterOrDigit() && it != '_'
        }

        return if (needsQuotes) {
            // Заменяем одинарные кавычки на двойные
            val escaped = sheetName.replace("'", "''")
            "'$escaped'"
        } else {
            sheetName
        }


    }

    // Преобразование номера колонки в букву (1->A, 2->B, ..., 27->AA)
    private fun getColumnLetter(columnNumber: Int): String {
        var temp = columnNumber
        var letter = ""

        while (temp > 0) {
            val modulo = (temp - 1) % 26
            letter = ('A'.code + modulo).toChar() + letter
            temp = (temp - modulo) / 26
        }

        return letter
    }

    // Поиск индекса строки по ID
    private fun findRowIndexById(idColumn: List<Any>, targetId: Long): Int {
        // Пропускаем заголовок (если он есть) и ищем ID
        for (i in idColumn.indices) {
            val cellValue = idColumn[i]
            when (cellValue) {
                is String -> {
                    val id = cellValue.toLongOrNull()
                    if (id == targetId) {
                        return i // Возвращаем индекс строки (0-based)
                    }
                }

                is Number -> {
                    if (cellValue.toLong() == targetId) {
                        return i
                    }
                }
            }
        }
        return -1 // Не найдено
    }

}


