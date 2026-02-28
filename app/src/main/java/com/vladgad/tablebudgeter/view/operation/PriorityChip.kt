package com.vladgad.tablebudgeter.view.operation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vladgad.tablebudgeter.view.data.Priority

@Composable
fun PriorityChip(
    priority: Priority,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = if (isSelected) 4.dp else 1.dp,
        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelectedChange(!isSelected) }
    ) {
        Text(
            text = priority.symbol,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
        )
    }
}
