package com.vladgad.tablebudgeter.view.operation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagMessageGeoRow(
    tagText: String, messageText: String, isGeoChecked: Boolean,
    onTagChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onGeoCheckedChange: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val tags = listOf("еда", "транспорт", "развлечения", "здоровье", "кафе")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp) // фиксированная высота для всех элементов
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Поле тега (30% ширины)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
        ) {
            OutlinedTextField(
                value = tagText,
                onValueChange = { onTagChange(it) },
                readOnly = false,
                placeholder = { Text("Тэг") },
                leadingIcon = { Icon(Icons.Default.Label, contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxSize()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                tags.forEach { tag ->
                    DropdownMenuItem(
                        text = { Text(tag, fontSize = 6.sp) },
                        onClick = {
                            onTagChange(tag)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Поле сообщения (50% ширины)
        OutlinedTextField(
            value = messageText,
            onValueChange = { onMessageChange(it) },
            placeholder = { Text("Сообщение") },
            leadingIcon = { Icon(Icons.Default.Message, contentDescription = null) },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight()
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Блок гео (20% ширины)
        Box(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(horizontal = 1.dp)
                )
                Checkbox(
                    checked = isGeoChecked,
                    onCheckedChange = { onGeoCheckedChange(it) },
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
