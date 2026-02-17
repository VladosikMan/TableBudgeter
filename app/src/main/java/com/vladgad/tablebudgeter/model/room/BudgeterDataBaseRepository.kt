package com.vladgad.tablebudgeter.model.room

import com.vladgad.tablebudgeter.model.OperationRepository
import com.vladgad.tablebudgeter.model.data.Operation
import com.vladgad.tablebudgeter.model.data.OperationStatus
import com.vladgad.tablebudgeter.model.room.OperationExtensions.Companion.toDomain
import com.vladgad.tablebudgeter.model.room.OperationExtensions.Companion.toEntity

class BudgeterDataBaseRepository() : OperationRepository {
    private val database: BudgeterDataBase by lazy {
        BudgeterDataBase.INSTANCE_ROOM_DATABASE
    }

    private val operationDao: OperationDAO by lazy {
        database.operationDAO()
    }


    override suspend fun insertOperation(operation: Operation): OperationStatus {
        return try {
            val entity = operation.toEntity() // Предполагается функция-расширение
            val id = operationDao.insertOperation(entity)
            return if(id>0)
                OperationStatus.Success(id)
            else
                OperationStatus.Error("Error", 0)
        } catch (e: Exception) {
            OperationStatus.Error(e.message ?: "Insert failed")
        }
    }

    override suspend fun insertOperations(operations: List<Operation>): OperationStatus {
        return try {
            val entities = operations.map { it.toEntity() }
            val ids = operationDao.insertOperations(entities)
            OperationStatus.Success(ids.last())
        } catch (e: Exception) {
            OperationStatus.Error("Ошибка массовой вставки: ${e.message ?: "unknown error"}")
        }
    }

    override suspend fun getOperation(id: Long): OperationStatus {
        return try {
            val entity = operationDao.getOperationById(id)
            if (entity != null) {
                val operation = entity.toDomain()
                OperationStatus.SuccessResult(operation.id, listOf(operation))
            } else {
                OperationStatus.Error("Операция с ID $id не найдена")
            }
        } catch (e: Exception) {
            OperationStatus.Error("Ошибка получения: ${e.message ?: "unknown error"}")
        }
    }

    override suspend fun getAllOperations(): OperationStatus {
        return try {
            val entities = operationDao.getAllOperations()
            val operations = entities.map { it.toDomain() }
            OperationStatus.SuccessResult(operations.size.toLong(), operations)
        } catch (e: Exception) {
            OperationStatus.Error("Ошибка загрузки всех операций: ${e.message ?: "unknown error"}")
        }
    }

    override suspend fun updateOperation(
        id: Long,
        operation: Operation
    ): OperationStatus {
        return try {
            // Проверяем существование операции
            val exists = operationDao.isOperationExists(id)
            if (!exists) {
                return OperationStatus.Error("Операция с ID $id не найдена для обновления")
            }

            // Создаем entity с правильным ID
            val entity = operation.copy(id = id).toEntity()
            val rowsUpdated = operationDao.updateOperation(entity)

            if (rowsUpdated > 0) {
                OperationStatus.Success(rowsUpdated.toLong())
            } else {
                OperationStatus.Error("Операция не была обновлена")
            }
        } catch (e: Exception) {
            OperationStatus.Error("Ошибка обновления: ${e.message ?: "unknown error"}")
        }
    }


    override suspend fun deleteOperation(id: Long): OperationStatus {
        return try {
            val rowsDeleted = operationDao.deleteOperationById(id)
            if (rowsDeleted > 0) {
                OperationStatus.Success(rowsDeleted.toLong())
            } else {
                OperationStatus.Error("Операция с ID $id не найдена для удаления")
            }
        } catch (e: Exception) {
            OperationStatus.Error("Ошибка удаления: ${e.message ?: "unknown error"}")
        }
    }

  /*  override suspend fun getOperationsCount(): Int {
        return 1
    }

    override suspend fun getTotalAmount(): Double {
        return 1.0
    }

    override suspend fun isOperationExists(id: Long): Boolean {
        return false
    }*/
}