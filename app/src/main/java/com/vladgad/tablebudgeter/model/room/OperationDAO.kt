package com.vladgad.tablebudgeter.model.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
@Dao
interface OperationDAO {

    // ============ INSERT операции ============

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operationEntity: OperationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperations(operations: List<OperationEntity>): List<Long>

    // ============ SELECT операции ============

    @Query("SELECT * FROM operations WHERE id = :id")
    suspend fun getOperationById(id: Long): OperationEntity?



    @Query("SELECT * FROM operations ORDER BY date_operation DESC")
    suspend fun getAllOperations(): List<OperationEntity>

    // Закомментированные методы из репозитория:
    // @Query("SELECT * FROM operations WHERE date_operation BETWEEN :from AND :to ORDER BY date_operation DESC")
    // suspend fun getOperationsByDateRange(from: Long, to: Long): List<OperationEntity>

    // @Query("SELECT * FROM operations WHERE type_operation = :type ORDER BY date_operation DESC")
    // suspend fun getOperationsByType(type: String): List<OperationEntity>

    // ============ UPDATE операции ============

    @Update
    suspend fun updateOperation(operationEntity: OperationEntity): Int



    // ============ DELETE операции ============

    @Delete
    suspend fun deleteOperation(operationEntity: OperationEntity): Int

    @Query("DELETE FROM operations WHERE id = :id")
    suspend fun deleteOperationById(id: Long): Int

    // @Query("DELETE FROM operations WHERE id IN (:ids)")
    // suspend fun deleteOperationsByIds(ids: List<Long>): Int

    @Query("SELECT EXISTS(SELECT 1 FROM operations WHERE id = :id LIMIT 1)")
    suspend fun isOperationExists(id: Long): Boolean

}
