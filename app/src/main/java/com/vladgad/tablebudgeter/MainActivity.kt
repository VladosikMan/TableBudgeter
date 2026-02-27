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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
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
import com.vladgad.tablebudgeter.Repository
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.OperationStatus
import com.vladgad.tablebudgeter.model.data.OperationStatus.Success
import com.vladgad.tablebudgeter.model.room.BudgeterDataBaseRepository
import com.vladgad.tablebudgeter.model.table.GoogleSheetsDatabaseRepository
import com.vladgad.tablebudgeter.model.table.SheetsServiceHelper
import com.vladgad.tablebudgeter.ui.theme.TableBudgeterTheme
import com.vladgad.tablebudgeter.view.AnalyticsScreen
import com.vladgad.tablebudgeter.view.HistoryScreen
import com.vladgad.tablebudgeter.view.OperationScreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import kotlin.text.toLong
import kotlin.time.Clock.System.now

class MainActivity : ComponentActivity() {
    private val sheetsHelper: GoogleSheetsDatabaseRepository = GoogleSheetsDatabaseRepository()
    private val repository = Repository()

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
    }

    @Composable
    fun DatabaseTestButtonsScreen() {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AnalyticsScreen()
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

}

