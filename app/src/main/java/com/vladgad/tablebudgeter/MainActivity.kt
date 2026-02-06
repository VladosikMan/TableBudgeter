package com.vladgad.tablebudgeter

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.vladgad.tablebudgeter.google.GoogleSignInUtils
import com.vladgad.tablebudgeter.google.GoogleSignInUtils.Companion.handleAuthorizationResult
import com.vladgad.tablebudgeter.google.GoogleSignInUtils.Companion.requestSheetsAuthorization
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.OperationStatus
import com.vladgad.tablebudgeter.model.room.BudgeterDataBaseRepository
import com.vladgad.tablebudgeter.model.table.GoogleSheetsDatabaseRepository
import com.vladgad.tablebudgeter.model.table.SheetsServiceHelper
import com.vladgad.tablebudgeter.ui.theme.TableBudgeterTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import kotlin.text.toLong
import kotlin.time.Clock.System.now

class MainActivity : ComponentActivity() {
    private val sheetsHelper: GoogleSheetsDatabaseRepository = GoogleSheetsDatabaseRepository()
    private lateinit var startAuthorizationIntent: ActivityResultLauncher<IntentSenderRequest>
    private fun onSuccess(authorizationResult: AuthorizationResult) {
        Toast.makeText(
            this,
            "Доступ к Google Sheets получен successful",
            Toast.LENGTH_LONG
        ).show()
        sheetsHelper.updateAccessToken(authorizationResult.accessToken!!)
    }

    private var pendingAction: (() -> Unit)? = null
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startAuthorizationIntent =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
                try {
                    handleAuthorizationResult(activityResult, pendingAction, this, ::onSuccess)
                } catch (e: ApiException) {
                    e.printStackTrace()
                    // log exception
                }
            }
        // Инициализация репозитория
        enableEdgeToEdge()
        setContent {
            TableBudgeterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        DatabaseTestButtonsScreen()
                    }
                }
            }
        }

        val context = this

        val mainActivity = this
        lifecycle.coroutineScope.launch {

            val res = GoogleSignInUtils.doGoogleSignIn(
                context = context,
            )
            if (res) {
                requestSheetsAuthorization(
                    onSuccess = ::onSuccess,
                    startAuthorizationIntent, mainActivity
                )
            } else
                Toast.makeText(context, "Аутентификация не прошла", Toast.LENGTH_LONG)
        }

        sheetsHelper.setId(
            resources.getString(R.string.google_sheet_id),
            resources.getInteger(R.integer.google_sheet_id_page).toLong()
        )
    }

    @Composable
    fun DatabaseTestButtonsScreen() {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            Button(onClick = {
                lifecycleScope.launch {
                    val success = sheetsHelper.insertOperation(generateTestOperations2()[0])
                    val x = 1
                }
            }) {
                Text(text = "Insert")
            }

            Button(onClick = {
                lifecycleScope.launch {
                    val success = sheetsHelper.insertOperations(generateTestOperations())
                    val x = 1
                }
            }) {
                Text(text = "Insert new ")
            }

            Button(onClick = {
                lifecycleScope.launch {
                    val success = sheetsHelper.getOperation(3)
                    val x = 1
                }
            }) {
                Text(text = "get row")
            }
            Button(onClick = {
                lifecycleScope.launch {
                    val success = sheetsHelper.getAllOperations()
                    val x = 1
                }
            }) {
                Text(text = "get all rows")
            }
            Button(onClick = {
                lifecycleScope.launch {
                    val success = sheetsHelper.updateOperation(7, generateTestOperations2()[0])
                    val x = 1
                }
            }) {
                Text(text = "update")
            }

            Button(onClick = {
                lifecycleScope.launch {
                    val success = sheetsHelper.deleteOperation(24)
                    val x = 1
                }
            }) {
                Text(text = "delte")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DatabaseTestButtonsPreview() {
        TableBudgeterTheme {
            // Для превью передаем null, т.к. нет контекста
            DatabaseTestButtonsScreen()
        }
    }

    fun generateTestOperations(): List<Operation> {
        val now = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L

        return listOf(
            // Расходы
            Operation(
                typeOperation = "Расход",
                dateOperation = now - (5 * oneDay),
                amount = 1500.0,
                account = "Сбербанк",
                tag = "Продукты",
                priority = 3,
                place = "Пятёрочка",
                message = "Еженедельные покупки"
            ),
            Operation(
                typeOperation = "Расход",
                dateOperation = now - (4 * oneDay),
                amount = 800.0,
                account = "Тинькофф",
                tag = "Кафе",
                priority = 2,
                place = "Starbucks",
                message = "Кофе с коллегами"
            ),
            Operation(
                typeOperation = "Расход",
                dateOperation = now - (3 * oneDay),
                amount = 2500.0,
                account = "Альфа-Банк",
                tag = "Транспорт",
                priority = 1,
                place = "Яндекс.Такси",
                message = "Поездка в аэропорт"
            ),
            Operation(
                typeOperation = "Расход",
                dateOperation = now - (2 * oneDay),
                amount = 3200.0,
                account = "Сбербанк",
                tag = "Развлечения",
                priority = 4,
                place = "Кинотеатр",
                message = "Билеты в кино"
            ),
            Operation(
                typeOperation = "Расход",
                dateOperation = now - oneDay,
                amount = 450.0,
                account = "Наличные",
                tag = "Транспорт",
                priority = 3,
                place = "Метро",
                message = "Пополнение транспортной карты"
            ),

            // Доходы
            Operation(
                typeOperation = "Доход",
                dateOperation = now - (6 * oneDay),
                amount = 75000.0,
                account = "Сбербанк",
                tag = "Зарплата",
                priority = 5,
                place = "Работа",
                message = "Ежемесячная зарплата"
            ),
            Operation(
                typeOperation = "Доход",
                dateOperation = now - (3 * oneDay),
                amount = 15000.0,
                account = "Тинькофф",
                tag = "Фриланс",
                priority = 4,
                place = "Дом",
                message = "Оплата за проект"
            ),
            Operation(
                typeOperation = "Доход",
                dateOperation = now - oneDay,
                amount = 5000.0,
                account = "Сбербанк",
                tag = "Кэшбэк",
                priority = 3,
                place = "Банк",
                message = "Кэшбэк за покупки"
            ),

            // Переводы
            Operation(
                typeOperation = "Перевод",
                dateOperation = now - (2 * oneDay),
                amount = 10000.0,
                account = "Сбербанк → Тинькофф",
                tag = "Накопительный счёт",
                priority = 3,
                place = "Мобильный банк",
                message = "Ежемесячные накопления"
            ),
            Operation(
                typeOperation = "Перевод",
                dateOperation = now - oneDay,
                amount = 5000.0,
                account = "Тинькофф → Альфа-Банк",
                tag = "Кредит",
                priority = 1,
                place = "Банк",
                message = "Погашение кредита"
            )
        )
    }

    fun generateTestOperations2(): List<Operation> {
        val now = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L

        return listOf(
            // Расходы
            Operation(
                typeOperation = "Магазин",
                dateOperation = now - (5 * oneDay),
                amount = -325.0,
                account = "Тбанк",
                tag = "Продукты",
                priority = 3,
                place = "Пятёрочка",
                message = "Еженедельные покупки",
                id = 7
            ),
        )
    }
}

