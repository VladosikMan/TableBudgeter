package com.vladgad.tablebudgeter.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vladgad.tablebudgeter.BudgeterApp
import com.vladgad.tablebudgeter.R

@Database(
    entities = [OperationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BudgeterDataBase : RoomDatabase() {
    abstract fun operationDAO(): OperationDAO

    companion object {
        val INSTANCE_ROOM_DATABASE: BudgeterDataBase by lazy {
            Room.databaseBuilder(
                BudgeterApp.instance.applicationContext,
                BudgeterDataBase::class.java,
                BudgeterApp.instance.resources.getString(R.string.database_budg)
            ).build()
        }
    }
}

