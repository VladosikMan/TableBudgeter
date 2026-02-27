package com.vladgad.tablebudgeter.viewmodel

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Copyright
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Work
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.vladgad.tablebudgeter.Repository
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.usecases.InsertOperationsUseCase
import com.vladgad.tablebudgeter.view.data.ChipElement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class OperationViewModel : ViewModel() {


    // Расширенный список категорий расходов
    val expenseCategories = listOf(
        ChipElement(Icons.Default.ShoppingCart, "Продукты"),
        ChipElement(Icons.Default.DirectionsCar, "Транспорт"),
        ChipElement(Icons.Default.LocalCafe, "Кафе"),
        ChipElement(Icons.Default.FitnessCenter, "Спорт"),
        ChipElement(Icons.Default.Movie, "Кино"),
        ChipElement(Icons.Default.Book, "Книги"),
        ChipElement(Icons.Default.Phone, "Связь"),
        ChipElement(Icons.Default.Home, "Жильё"),
        ChipElement(Icons.Default.Pets, "Зоотовары"),
        ChipElement(Icons.Default.HealthAndSafety, "Здоровье"),
        ChipElement(Icons.Default.ShoppingBag, "Одежда"),
        ChipElement(Icons.Default.Face, "Косметика"),
        ChipElement(Icons.Default.CardGiftcard, "Подарки"),
        ChipElement(Icons.Default.Flight, "Путешествия"),
        ChipElement(Icons.Default.LocalGasStation, "Автомобиль"),
        ChipElement(Icons.Default.Security, "Страхование"),
        ChipElement(Icons.Default.AttachMoney, "Налоги"),
        ChipElement(Icons.Default.Build, "Ремонт"),
        ChipElement(Icons.Default.Palette, "Хобби"),
        ChipElement(Icons.Default.LocalBar, "Алкоголь"),
        ChipElement(Icons.Default.VolunteerActivism, "Благотворительность"),
        ChipElement(Icons.Default.MoreHoriz, "Прочее")
    )

    // Расширенный список категорий доходов
    val incomeCategories = listOf(
        ChipElement(Icons.Default.Work, "Зарплата"),
        ChipElement(Icons.Default.Business, "Бизнес"),
        ChipElement(Icons.Default.AttachMoney, "Фриланс"),
        ChipElement(Icons.Default.Store, "Подработка"),
        ChipElement(Icons.Default.TrendingUp, "Инвестиции"),
        ChipElement(Icons.Default.Money, "Дивиденды"),
        ChipElement(Icons.Default.AccountBalance, "Проценты по вкладам"),
        ChipElement(Icons.Default.CardGiftcard, "Подарки"),
        ChipElement(Icons.Default.SwapHoriz, "Возврат долгов"),
        ChipElement(Icons.Default.Sell, "Продажа вещей"),
        ChipElement(Icons.Default.Redeem, "Кэшбэк"),
        ChipElement(Icons.Default.VolunteerActivism, "Социальные выплаты"),
        ChipElement(Icons.Default.House, "Аренда"),
        ChipElement(Icons.Default.Copyright, "Роялти"),
        ChipElement(Icons.Default.EmojiEvents, "Выигрыши"),
        ChipElement(Icons.Default.MoreHoriz, "Прочие доходы")
    )
    val accounts = listOf(
        ChipElement(Icons.Default.AccountBalance, "Т-Банк"),
        ChipElement(Icons.Default.AccountBalanceWallet, "Сбер"),
        ChipElement(Icons.Default.Business, "ВТБ"),
        ChipElement(Icons.Default.Money, "Наличка"),
    )

    private val mTag = "OperationViewModel"
    private val _operationData = MutableStateFlow<OperationData>(
        OperationData(
            typeOperation = -1,
            typeAccount = 0, priority = 2, amount = "0", tag = "", message = "", geoStatus = false
        )
    )
    val operationData: StateFlow<OperationData> = _operationData.asStateFlow()
    private val _statusInsertOperation = MutableStateFlow(false)
    val statusInsertOperation: StateFlow<Boolean> = _statusInsertOperation.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    fun openBottomSheet() {
        _showBottomSheet.value = true
    }

    fun closeBottomSheet() {
        _showBottomSheet.value = false
    }

    fun updateTypeOperation(type: Int) {
        _operationData.update { it.copy(typeOperation = type) }
    }

    fun updateAccount(type: Int) {
        _operationData.update { it.copy(typeAccount = type) }
    }

    fun updatePriority(type: Int) {
        _operationData.update { it.copy(priority = type) }
    }

    fun updateTag(tag: String) {
        _operationData.update { it.copy(tag = tag) }
    }

    fun updateMessage(message: String) {
        _operationData.update { it.copy(message = message) }
    }

    fun updateGeoStatus(geoStatus: Boolean) {
        _operationData.update { it.copy(geoStatus = geoStatus) }
    }

    fun updateAmount(amount: String) {
        _operationData.update { it.copy(amount = amount) }
    }

    fun insertOperation(modif : Byte) {
        val operationData = _operationData.value
        val categories = if(modif.toInt() == -1)
            expenseCategories
        else
            incomeCategories
        val operation = Operation(
            typeOperation = categories[operationData.typeOperation].text,
            dateOperation = Date().time,
            amount = operationData.amount.toDouble() * modif,
            account = accounts[operationData.typeAccount].text,
            priority = operationData.priority,
            tag = operationData.tag,
            message = operationData.message
        )
        Log.d(mTag, Gson().toJson(operation))
        viewModelScope.launch {
            val result =
                InsertOperationsUseCase(Repository.INSTANCE_REPOSITORY).invoke(listOf(operation))
            _statusInsertOperation.update {
                true
            }
            _showBottomSheet.update{
                false
            }
        }
    }

    fun updateOperationStatus() {
        _statusInsertOperation.update {
            false
        }
    }
}
