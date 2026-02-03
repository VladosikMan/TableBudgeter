package com.vladgad.tablebudgeter.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vladgad.tablebudgeter.R

@Database(
    entities = [OperationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BudgeterDataBase : RoomDatabase() {
    abstract fun operationDAO(): OperationDAO

    companion object {
        @Volatile
        private var INSTANCE: BudgeterDataBase? = null

        fun getInstance(context: Context): BudgeterDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgeterDataBase::class.java,
                    context.resources.getString(R.string.database_budg)
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

