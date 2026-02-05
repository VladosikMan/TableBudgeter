package com.vladgad.tablebudgeter.model.table

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject

import com.vladgad.tablebudgeter.http.KtorClient.Companion.getInstanceClientSheets
import com.vladgad.tablebudgeter.model.table.SheetRequestBuilder.createHeadersRequest
import com.vladgad.tablebudgeter.utils.GsonClient.Companion.getInstanceGson
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SheetsServiceHelper(private var accessToken: String?) {
    private val headers =
        listOf("Действие", "Дата", "Сумма", "Счёт", "Приоритет", "Тэг", "Место", "Сообщение", "Id")

    private val client = getInstanceClientSheets()
    private val gson = getInstanceGson()

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

    // 3. ЗАПИСАТЬ ДАННЫЕ
    suspend fun writeData(
        spreadsheetId: String,
        range: String,
        values: List<List<Any>>
    ): Boolean = withContext(Dispatchers.IO) {
        val token = accessToken ?: return@withContext false

        val encodedRange = range.encodeURLParameter()
        val url =
            "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId/values/$encodedRange"

        // Формируем тело запроса
        val requestBody = JsonObject().apply {
            add("values", gson.toJsonTree(values))
        }

        return@withContext try {
            val response: HttpResponse = client.put(url) {
                header(HttpHeaders.Authorization, "Bearer $token")
                parameter("valueInputOption", "RAW")
                setBody(gson.toJson(requestBody))
            }

            response.status.isSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
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


    // Добавьте этот метод в ваш класс SheetsServiceHelper
    suspend fun addNewSheetSimple(
        spreadsheetId: String,
        title: String,
        index: Int? = null,
        rowCount: Int = 100000,
        columnCount: Int = 12
    ): Int? = withContext(Dispatchers.IO) {
        val token = accessToken ?: return@withContext null

        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId:batchUpdate"

        // Создаем JSON запрос напрямую через Gson JsonObject
        val requestJson = JsonObject().apply {
            val requestsArray = JsonArray()
            val requestObject = JsonObject().apply {
                val addSheetObject = JsonObject().apply {
                    val propertiesObject = JsonObject().apply {
                        addProperty("title", title)

                        // Добавляем index, только если он не null
                        index?.let { addProperty("index", it) }

                        // Добавляем gridProperties
                        val gridProperties = JsonObject().apply {
                            addProperty("rowCount", rowCount)
                            addProperty("columnCount", columnCount)
                        }
                        add("gridProperties", gridProperties)
                    }
                    add("properties", propertiesObject)
                }
                add("addSheet", addSheetObject)
            }
            requestsArray.add(requestObject)
            add("requests", requestsArray)
        }

        return@withContext try {
            val response: String = client.post(url) {
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                setBody(gson.toJson(requestJson))
            }.body()

            // Парсим ответ и извлекаем sheetId
            val jsonResponse = gson.fromJson(response, JsonObject::class.java)
            val replies = jsonResponse.getAsJsonArray("replies")

            if (replies != null && replies.size() > 0) {
                val firstReply = replies.get(0).asJsonObject
                val addSheet = firstReply.getAsJsonObject("addSheet")
                addSheet?.getAsJsonObject("properties")?.get("sheetId")?.asInt
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Вспомогательная функция для создания ячейки с текстом
    private fun createCell(value: String): JsonObject {
        return JsonObject().apply {
            add("userEnteredValue", JsonObject().apply {
                addProperty("stringValue", value)
            })
        }
    }

    suspend fun insertEmptyRow(
        spreadsheetId: String,
        sheetId: Int,
        rowIndex: Int
    ): Boolean = withContext(Dispatchers.IO) {
        val token = accessToken ?: return@withContext false

        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId:batchUpdate"

        val requestBody = JsonObject().apply {
            add("requests", JsonArray().apply {
                add(JsonObject().apply {
                    add("insertDimension", JsonObject().apply {
                        add("range", JsonObject().apply {
                            addProperty("sheetId", sheetId)
                            addProperty("dimension", "ROWS")
                            addProperty("startIndex", rowIndex)
                            addProperty("endIndex", rowIndex + 1) // Вставляем 1 строку
                        })
                        addProperty("inheritFromBefore", false)
                    })
                })
            })
        }

        return@withContext try {
            val response: HttpResponse = client.post(url) {
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                setBody(gson.toJson(requestBody))
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun addDataRow(
        spreadsheetId: String,
        sheetId: Int, // Используем sheetId
        rowIndex: Int,
        values: List<Any>,
        insertRow: Boolean = true
    ): Boolean = withContext(Dispatchers.IO) {
        // 1. Если нужно, вставляем пустую строку (уже использует sheetId)
        if (insertRow) {
            val insertSuccess = insertEmptyRow(spreadsheetId, sheetId, rowIndex)
            if (!insertSuccess) return@withContext false
        }

        // 2. Записываем данные через batchUpdate, указывая sheetId
        val token = accessToken ?: return@withContext false

        val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId:batchUpdate"

        // Создаем запрос на обновление ячеек с привязкой к sheetId
        val requestBody = JsonObject().apply {
            add("requests", JsonArray().apply {
                add(JsonObject().apply {
                    add("updateCells", JsonObject().apply {
                        // Указываем область: привязываемся к sheetId и строке
                        add("range", JsonObject().apply {
                            addProperty("sheetId", sheetId)
                            addProperty("startRowIndex", rowIndex)
                            addProperty("endRowIndex", rowIndex + 1) // Одна строка
                            addProperty("startColumnIndex", 0) // Колонка A (0)
                            addProperty("endColumnIndex", 4)   // До колонки D (4, т.к. 4 элемента)
                        })
                        addProperty("fields", "userEnteredValue")
                        // Данные строки
                        add("rows", JsonArray().apply {
                            add(JsonObject().apply {
                                add("values", JsonArray().apply {
                                    values.forEach { value ->
                                        add(JsonObject().apply {
                                            add("userEnteredValue", JsonObject().apply {
                                                when (value) {
                                                    is String -> addProperty("stringValue", value)
                                                    is Number -> addProperty("numberValue", value)
                                                    else -> addProperty(
                                                        "stringValue",
                                                        value.toString()
                                                    )
                                                }
                                            })
                                        })
                                    }
                                })
                            })
                        })
                    })
                })
            })
        }

        return@withContext try {
            val response: HttpResponse = client.post(url) {
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                setBody(gson.toJson(requestBody))
            }
            // ... (обработка ответа с логированием ошибок, как обсуждалось ранее)
            response.status.isSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun close() {
        client.close()
    }

}

// Класс для более типобезопасного подхода (опционально)
data class SpreadsheetInfo(
    val spreadsheetId: String,
    val properties: SpreadsheetProperties,
    val sheets: List<SheetInfo>
)

data class SpreadsheetProperties(
    val title: String,
    val locale: String? = null,
    val timeZone: String? = null
)

data class SheetInfo(
    val properties: SheetProperties,
    val data: List<GridData>? = null
)

data class SheetProperties(
    val sheetId: Int,
    val title: String,
    val index: Int,
    val gridProperties: GridProperties? = null
)

data class GridProperties(
    val rowCount: Int,
    val columnCount: Int
)

data class GridData(
    val startRow: Int,
    val startColumn: Int,
    val rowData: List<RowData>
)


