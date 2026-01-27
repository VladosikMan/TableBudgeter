package com.vladgad.tablebudgeter

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private lateinit var startAuthorizationIntent: ActivityResultLauncher<IntentSenderRequest>

class MainActivity : ComponentActivity() {
    private val TAG = "PepeNis"
    private lateinit var sheetsHelper: SheetsServiceHelper
    private var pendingAction: (() -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sheetsHelper = SheetsServiceHelper(null)
        startAuthorizationIntent =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
                try {
                    handleAuthorizationResult(activityResult)
                } catch (e: ApiException) {
                    e.printStackTrace()
                    // log exception
                }
            }
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MainScreen(modifier = Modifier.padding(innerPadding))
            }

        }
    }

    private fun requestSheetsAuthorization(onSuccess: () -> Unit) {
        // Сохраняем действие на случай, если потребуется диалог согласия
        pendingAction = onSuccess

        // Scope для доступа к Google Sheets
        val requestedScopes: List<Scope> = listOf(
            Scope("https://www.googleapis.com/auth/spreadsheets"),
            Scope("https://www.googleapis.com/auth/drive.readonly")
        )

        val authorizationRequest = AuthorizationRequest.Builder()
            .setRequestedScopes(requestedScopes)
            .build()

        Identity.getAuthorizationClient(this)
            .authorize(authorizationRequest)
            .addOnSuccessListener { authorizationResult ->
                if (authorizationResult.hasResolution()) {
                    // Показываем диалог согласия Google
                    val pendingIntent = authorizationResult.pendingIntent
                    startAuthorizationIntent.launch(
                        IntentSenderRequest.Builder(pendingIntent!!.intentSender).build()
                    )
                } else {
                    // Доступ уже предоставлен ранее
                    handleAuthorizationSuccess(authorizationResult)
                    onSuccess()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to authorize", e)
                Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show()
                pendingAction = null
            }
    }

    // Обработка результата диалога согласия (из документации Google)
    private fun handleAuthorizationResult(activityResult: ActivityResult) {
        try {
            if (activityResult.resultCode == RESULT_OK) {
                val authorizationResult = Identity.getAuthorizationClient(this)
                    .getAuthorizationResultFromIntent(activityResult.data)

                handleAuthorizationSuccess(authorizationResult)

                // Выполняем отложенное действие
                pendingAction?.invoke()
            } else {
                Toast.makeText(this, "Авторизация отменена", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Authorization failed", e)
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pendingAction = null
        }
    }

    private fun handleAuthorizationSuccess(authorizationResult: AuthorizationResult) {


        sheetsHelper.updateAccessToken(authorizationResult.accessToken)

    }

    private fun createNewSpreadsheet() {
        lifecycleScope.launch {


            // Создаем таблицу
            val tableTitle = "Моя таблица ${System.currentTimeMillis()}"
            val spreadsheetId = sheetsHelper.createSpreadsheet(tableTitle)

            runOnUiThread {
                if (spreadsheetId != null) {
                    Log.d(TAG, "Таблица создана!\nID: $spreadsheetId")
                } else {
                    //  binding.tvStatus.text = "Ошибка создания таблицы"
                    Toast.makeText(
                        this@MainActivity,
                        "Не удалось создать таблицу", Toast.LENGTH_SHORT
                    ).show()
                }
                //  showLoading(false)
            }
        }
    }

    private suspend fun getTablesId() {

        // 1. Получить полный список с деталями
        val spreadsheets = sheetsHelper.getSpreadsheetsList()
        if (spreadsheets.isNotEmpty()) {

        } else {
            Log.d(TAG, "Пустой список (ID -> название):")
        }
    }

    private suspend fun createNewPage() {

        val newSheetId = sheetsHelper.addNewSheetSimple(
            spreadsheetId = "1q3EVetq4BIz0KtUWXABVmuPC3xtBVdna1UqdnLwlgqE",
            title = "Budg1",
            index = 1, // Поместить на вторую позицию
        )

        if (newSheetId != null) {
            Log.d(TAG, "✅ Лист создан! Sheet ID: $newSheetId")
            // Дополнительные действия с новым листом
        } else {
            Log.e(TAG, "❌ Ошибка создания листа")
        }

    }

    private suspend fun createHeader() {
        val success = sheetsHelper.writeHeaderRowBySheetId(
            "1q3EVetq4BIz0KtUWXABVmuPC3xtBVdna1UqdnLwlgqE",
            1410545626,
            "A10"
        ) // Записать, например, на 1
        runOnUiThread {
            if (success) {
                Toast.makeText(
                    this@MainActivity,
                    "Заголовки добавлены", Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Ошибка добавления заголовков", Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun addNewExpense() {
        lifecycleScope.launch {

            val spreadsheetId = "1q3EVetq4BIz0KtUWXABVmuPC3xtBVdna1UqdnLwlgqE"
            val newRowData = listOf(
                "Пенрим",  // Действие
                "2025-01-26",         // Дата
                57,              // Сумма (число!)
                "Сбербанк" // Место
            )

            // Добавляем строку в конец (например, после заголовков в строке 0)
            // rowIndex = 1 означает: вставить строку под первой строкой (заголовками)
            val success = sheetsHelper.addDataRow(
                spreadsheetId = spreadsheetId,
                sheetId = 1410545626,
                rowIndex = 1, // Вставить ПОД строкой 0 (заголовками)
                values = newRowData,
                insertRow = true // Вставлять новую строку, а не перезаписывать существующую
            )

            runOnUiThread {
                if (success) {
                    Toast.makeText(
                        this@MainActivity,
                        "Запись добавлена!", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Ошибка добавления записи", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    @Composable
    fun MainScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        Column(
            modifier = modifier.fillMaxSize(),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

//            Button(onClick = {
//                GoogleSignInUtils.doGoogleSignIn(
//                    context = context,
//                    scope = scope,
//                    //launcher = launcher,
//                    login = {
//                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
//                    }
//                )
//            }) {
//                Text(text = "Просто сигн без гугла кнопки")
//            }
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
//            Button(onClick = {
//                GoogleSignInUtils.clearState(
//                    context = context,
//                    scope = scope,
////                launcher = launcher,
//                    clear = {
//                        Toast.makeText(context, "Clear successful", Toast.LENGTH_LONG).show()
//                    }
//                )
//            }) {
//                Text(text = "Чистим данные")
//            }
            Button(onClick = {
                requestSheetsAuthorization(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Доступ к Google Sheets получен successful",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }) {
                Text(text = "Авторимся а затем пытаемся таблить ")
            }


            Button(onClick = {
                createNewSpreadsheet()
            }) {
                Text(text = "Пытаемся таблитьбля ")
            }

            Button(onClick = {
                lifecycleScope.launch {
                    getTablesId()
                }
            }) {
                Text(text = "Получить список таблов ")
            }


            Button(onClick = {
                lifecycleScope.launch {
                    createNewPage()
                }
            }) {
                Text(text = "Создать новую страницу в табле ")
            }
            Button(onClick = {
                lifecycleScope.launch {
                    createHeader()
                }
            }) {
                Text(text = "Добавить заголовОчка")
            }
            Button(onClick = {
                lifecycleScope.launch {
                    addNewExpense()
                }
            }) {
                Text(text = "addNewExpenseу")
            }

            SimpleForm()

        }
    }

    //================================

    @Composable
    fun SimpleForm() {
        var action by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var selectedPlace by remember { mutableStateOf("Сбербанк") }
        var loading by remember { mutableStateOf(false) }
        var message by remember { mutableStateOf("") }

        val places = listOf("Сбербанк", "ВТБ", "Тбанк", "Наличка")
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Поле ввода действия
            OutlinedTextField(
                value = action,
                onValueChange = { action = it },
                label = { Text("Действие") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Поле ввода суммы
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Сумма") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Простой выбор места через Row с кнопками
            Text("Источник:", modifier = Modifier.padding(bottom = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),

                ) {
                places.forEach { place ->
                    FilterChip(
                        selected = selectedPlace == place,
                        onClick = { selectedPlace = place },
                        label = { Text(place) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка
            Button(
                onClick = {
                    if (action.isBlank() || amount.isBlank()) {
                        message = "Заполните все поля"
                        return@Button
                    }

                    loading = true
                    message = "Добавляем..."

                    scope.launch {
                        try {
                            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(Date())

                            // Замените на ваши реальные ID
                            val spreadsheetId = "1q3EVetq4BIz0KtUWXABVmuPC3xtBVdna1UqdnLwlgqE"
                            val sheetId = 1410545626

                            // Вставляем строку (rowIndex = 1 - под заголовками)
                            val insertSuccess =
                                sheetsHelper.insertEmptyRow(spreadsheetId, sheetId, 1)

                            if (insertSuccess) {
                                // Записываем данные
                                val writeSuccess = sheetsHelper.addDataRow(
                                    spreadsheetId = spreadsheetId,
                                    sheetId = sheetId,
                                    rowIndex = 1,
                                    values = listOf(
                                        action,
                                        currentDate,
                                        amount.toDouble(),
                                        selectedPlace
                                    ),
                                    insertRow = false
                                )

                                message = if (writeSuccess) "✅ Добавлено" else "❌ Ошибка"

                                if (writeSuccess) {
                                    action = ""
                                    amount = ""
                                    selectedPlace = "Сбербанк"
                                }
                            } else {
                                message = "❌ Ошибка вставки строки"
                            }
                        } catch (e: Exception) {
                            message = "❌ Ошибка: ${e.message}"
                        } finally {
                            loading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Добавить запись")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Сообщение о статусе
            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = when {
                        message.startsWith("✅") -> MaterialTheme.colorScheme.primary
                        message.startsWith("❌") -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }

}






