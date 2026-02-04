package com.vladgad.tablebudgeter

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vladgad.tablebudgeter.google.GoogleSignInUtils
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        //
        Button(onClick = {
            GoogleSignInUtils.doGoogleSignIn(
                context = context,
                scope = scope,
                //launcher = launcher,
                login = {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                }
            )
        }) {
            Text(text = "Просто сигн без гугла кнопки")
        }
        Button(onClick = {
            GoogleSignInUtils.doGoogleSignInBottom(
                context = context,
                scope = scope,
                // launcher = launcher,
                login = {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_LONG).show()
                }
            )
        }) {
            Text(text = "Сигн с гугла кнопкой")
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
        message = "Тестовая операция создана ${
            SimpleDateFormat(
                "HH:mm:ss",
                Locale.getDefault()
            ).format(Date())
        }"
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