package com.vladgad.tablebudgeter.view.operation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vladgad.tablebudgeter.view.data.ChipElement

@Composable
fun CategoryGridSelector(
    selectedIndex: Int,
    onTypeOperationChange: (Int) -> Unit
) {
    // Список категорий: иконка, название
    val categories = listOf(
        ChipElement(Icons.Default.ShoppingCart, "Продукты"),
        ChipElement(Icons.Default.DirectionsCar, "Транспорт"),
        ChipElement(Icons.Default.LocalCafe, "Кафе"),
        ChipElement(Icons.Default.FitnessCenter, "Спорт"),
        ChipElement(Icons.Default.Movie, "Кино"),
        ChipElement(Icons.Default.Book, "Книги"),
        ChipElement(Icons.Default.Phone, "Связь"),
        ChipElement(Icons.Default.Home, "Жильё"),
        ChipElement(Icons.Default.Pets, "Зоотовары"),
        ChipElement(Icons.Default.HealthAndSafety, "Здоровье")
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(4), // ровно 4 столбца
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(categories) { index, (icon, label) ->
            OperationTypeElement(
                icon = icon,
                typeOperation = label,
                isSelected = selectedIndex == index,
                onSelectedChange = { isSelected ->
                    // Если выбрали этот элемент, передаём его индекс, иначе -1
                    onTypeOperationChange(if (isSelected) index else -1)
                }
            )
        }
    }
}