package com.vladgad.tablebudgeter.model.room

import com.vladgad.tablebudgeter.model.data.Operation

class OperationExtensions {
    // Operation -> OperationEntity
    companion object {
        fun Operation.toEntity(): OperationEntity {
            return OperationEntity(
                id = this.id,
                typeOperation = this.typeOperation,
                dateOperation = this.dateOperation,
                amount = this.amount,
                account = this.account,
                tag = this.tag.takeIf { it.isNotBlank() } ?: "",
                priority = this.priority,
                place = this.place.takeIf { it.isNotBlank() } ?: "",
                message = this.message.takeIf { it.isNotBlank() } ?: ""
            )
        }

        // OperationEntity -> Operation
        fun OperationEntity.toDomain(): Operation {
            return Operation(
                id = this.id,
                typeOperation = this.typeOperation,
                dateOperation = this.dateOperation,
                amount = this.amount,
                account = this.account,
                tag = this.tag ?: "",
                priority = this.priority ?: 3,
                place = this.place ?: "",
                message = this.message ?: ""
            )
        }
    }
}