package com.vladgad.tablebudgeter.data

data class Operation(
    val typeOperation: String,
    val dateOperation: String,
    val amount: Double,
    val account: String,
    val id: Long,
    val tag: String = "",
    val priority: Int = 3,
    val place: String = "",
    val message: String = ""
) {
    init {
        require(priority in 1..5) { "Приоритет должен быть от 1 до 5: $priority" }
    }
}
