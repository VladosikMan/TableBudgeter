package com.vladgad.tablebudgeter.model.data

enum class InsertStatusOperationEnum(val code : Int) {
    ALL_REPOSITORY_SUCCESS(0),
    GOOGLE_ERROR(1),
    ROOM_ERROR(2),
    INSERT_ERROR(3),
    FUNC_ERROR(4),
}
