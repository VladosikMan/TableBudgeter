package com.vladgad.tablebudgeter.model.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "operations")
data class OperationEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    @ColumnInfo(name = "type_operation")
    val typeOperation: String,
    @ColumnInfo(name = "date_operation")
    val dateOperation: Long,
    val amount: Double,
    val account: String,

    val tag: String? = "",
    val priority: Int? = 3,
    val place: String? = "",
    val message: String? = ""

)