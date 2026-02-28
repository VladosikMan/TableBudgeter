package com.vladgad.tablebudgeter

import android.annotation.SuppressLint
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
import com.vladgad.tablebudgeter.view.CompactBottomBar
import com.vladgad.tablebudgeter.view.HistoryScreen
import com.vladgad.tablebudgeter.view.OperationScreen
import com.vladgad.tablebudgeter.view.data.NavItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import kotlin.text.toLong
import kotlin.time.Clock.System.now

class MainActivity : ComponentActivity() {
    private lateinit var startAuthorizationIntent: ActivityResultLauncher<IntentSenderRequest>
    private fun onSuccess(authorizationResult: AuthorizationResult) {
        Toast.makeText(
            this,
            "Доступ к Google Sheets получен successful",
            Toast.LENGTH_LONG
        ).show()
        Repository.INSTANCE_REPOSITORY.updateGoogleToken(authorizationResult.accessToken!!)

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
                    ) {
                        MainScreen()
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
}

// Главный экран с навигацией и нижней панелью
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        NavItem("Записи", Icons.Default.History),
        NavItem("Добавить", Icons.Default.Add), // центральный
        NavItem("Отчеты", Icons.Default.Analytics)
    )

    // Получаем текущий маршрут для подсветки выбранного пункта
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedIndex = when (currentRoute) {
        "history" -> 0
        "operation" -> 1
        "analytics" -> 2
        else -> 0 // по умолчанию история
    }
    Scaffold(
        bottomBar = {
            CompactBottomBar(
                items = items,
                selectedIndex = selectedIndex,
                onItemClick = { index ->
                    val route = when (index) {
                        0 -> "history"
                        1 -> "operation"
                        2 -> "analytics"
                        else -> "history"
                    }
                    // Корректная навигация для bottom bar
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        // NavHost с учётом отступов от панели
        NavHost(
            navController = navController,
            startDestination = "history",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("history") { HistoryScreen() }
            composable("operation") { OperationScreen() }
            composable("analytics") { AnalyticsScreen() }
        }
    }
}