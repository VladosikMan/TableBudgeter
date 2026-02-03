package com.vladgad.tablebudgeter

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.OperationStatus
import com.vladgad.tablebudgeter.model.room.BudgeterDataBaseRepository
import com.vladgad.tablebudgeter.ui.theme.TableBudgeterTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private lateinit var repository: BudgeterDataBaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация репозитория
        repository = BudgeterDataBaseRepository(applicationContext)

        enableEdgeToEdge()
        setContent {
            TableBudgeterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        DatabaseTestButtonsScreen(repository)
                    }
                }
            }
        }
    }
}

@Composable
fun DatabaseTestButtonsScreen(repository: BudgeterDataBaseRepository) {
    // Для работы с корутинами в Compose
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Кнопка 1: Добавить тестовую операцию
        Button(
            onClick = {
                coroutineScope.launch {
                    val operation = createTestOperation()
                    when (val result = repository.insertOperation(operation)) {
                        is OperationStatus.Success -> {
                            Log.d("DB_TEST", "✅ Операция добавлена, ID: ${result.id}")
                        }

                        is OperationStatus.Error -> {
                            Log.e("DB_TEST", "❌ Ошибка добавления: ${result.message}")
                        }

                        else -> {}
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить тестовую операцию")
        }

        // Кнопка 2: Получить все операции
        Button(
            onClick = {
                coroutineScope.launch {
                    when (val result = repository.getAllOperations()) {
                        is OperationStatus.SuccessResult -> {
                            Log.d("DB_TEST", "📋 Найдено ${result.listResult.size} операций:")
                            result.listResult.forEach { operation ->
                                Log.d("DB_TEST", "   - ${operation.typeOperation}: ${operation.amount} (ID: ${operation.id})")
                            }
                        }
                        is OperationStatus.Error -> {
                            Log.e("DB_TEST", "❌ Ошибка получения: ${result.message}")
                        }
                        else -> {
                            Log.d("DB_TEST", "⚠️ Неожиданный результат: $result")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Получить все операции")
        }

        // Кнопка 3: Получить операцию по ID
        Button(
            onClick = {
                coroutineScope.launch {
                    // Используем текущее время как тестовый ID
                    val testId = System.currentTimeMillis()
                    when (val result = repository.getOperation(testId)) {
                        is OperationStatus.SuccessResult -> {
                            if (result.listResult.isNotEmpty()) {
                                val operation = result.listResult.first()
                                Log.d("DB_TEST", "🔍 Найдена операция: ID=${operation.id}, тип=${operation.typeOperation}, сумма=${operation.amount}")
                            } else {
                                Log.d("DB_TEST", "🔍 Операция с ID $testId не найдена")
                            }
                        }
                        is OperationStatus.Error -> {
                            Log.d("DB_TEST", "🔍 ${result.message}")
                        }
                        else -> {
                            Log.d("DB_TEST", "⚠️ Неожиданный результат: $result")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Найти по ID (тест)")
        }

        // Кнопка 4: Добавить несколько операций
        Button(
            onClick = {
                coroutineScope.launch {
                    val operations = listOf(
                        createTestOperation(type = "income", amount = 500.0),
                        createTestOperation(type = "expense", amount = -150.0),
                        createTestOperation(type = "income", amount = 750.0)
                    )

                    when (val result = repository.insertOperations(operations)) {
                        is OperationStatus.Success -> {
                            Log.d("DB_TEST", "✅ Добавлено несколько операций, последний ID: ${result.id}")
                        }
                        is OperationStatus.Error -> {
                            Log.e("DB_TEST", "❌ Ошибка массовой вставки: ${result.message}")
                        }
                        else -> {
                            Log.d("DB_TEST", "⚠️ Неожиданный результат: $result")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить несколько операций")
        }

        // Кнопка 5: Обновить операцию
        Button(
            onClick = {
                coroutineScope.launch {
                    // Сначала получаем существующую операцию
                    when (val allResult = repository.getAllOperations()) {
                        is OperationStatus.SuccessResult -> {
                            if (allResult.listResult.isNotEmpty()) {
                                val firstOperation = allResult.listResult.first()
                                val updatedOperation = firstOperation.copy(
                                    amount = firstOperation.amount + 100.0,
                                    message = "Обновлено в ${Date()}"
                                )

                                val updateResult = repository.updateOperation(firstOperation.id, updatedOperation)

                                when (updateResult) {
                                    is OperationStatus.SuccessUpdateDelete -> {
                                        Log.d("DB_TEST", "✏️ Операция обновлена, затронуто строк: ${updateResult.count}")
                                    }
                                    is OperationStatus.Error -> {
                                        Log.e("DB_TEST", "❌ Ошибка обновления: ${updateResult.message}")
                                    }
                                    else -> {
                                        Log.d("DB_TEST", "⚠️ Неожиданный результат: $updateResult")
                                    }
                                }
                            } else {
                                Log.d("DB_TEST", "📭 Нет операций для обновления")
                            }
                        }
                        is OperationStatus.Error -> {
                            Log.e("DB_TEST", "❌ Ошибка получения операций: ${allResult.message}")
                        }

                        else -> {}
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Обновить первую операцию")
        }

        // Кнопка 6: Удалить операцию
        Button(
            onClick = {
                coroutineScope.launch {
                    when (val allResult = repository.getAllOperations()) {
                        is OperationStatus.SuccessResult -> {
                            if (allResult.listResult.isNotEmpty()) {
                                val firstOperation = allResult.listResult.first()
                                val deleteResult = repository.deleteOperation(firstOperation.id)

                                when (deleteResult) {
                                    is OperationStatus.SuccessUpdateDelete -> {
                                        Log.d("DB_TEST", "🗑️ Операция удалена, затронуто строк: ${deleteResult.count}")
                                    }
                                    is OperationStatus.Error -> {
                                        Log.e("DB_TEST", "❌ Ошибка удаления: ${deleteResult.message}")
                                    }
                                    else -> {
                                        Log.d("DB_TEST", "⚠️ Неожиданный результат: $deleteResult")
                                    }
                                }
                            } else {
                                Log.d("DB_TEST", "📭 Нет операций для удаления")
                            }
                        }
                        is OperationStatus.Error -> {
                            Log.e("DB_TEST", "❌ Ошибка получения операций: ${allResult.message}")
                        }

                        else -> {}
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Удалить первую операцию")
        }
    }
}

// Функция для создания тестовой операции
private fun createTestOperation(
    type: String = "income",
    amount: Double = 1000.0,
    account: String = "Test Account"
): Operation {
    return Operation(
        id = Date().time + Random.nextLong(1000), // Уникальный ID
        typeOperation = type,
        dateOperation = System.currentTimeMillis(),
        amount = amount,
        account = account,
        tag = "test",
        priority = Random.nextInt(1, 6),
        place = "Test Place",
        message = "Тестовая операция создана ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}"
    )
}

@Preview(showBackground = true)
@Composable
fun DatabaseTestButtonsPreview() {
    TableBudgeterTheme {
        // Для превью передаем null, т.к. нет контекста
        DatabaseTestButtonsScreen(BudgeterDataBaseRepository(Application()))
    }
}