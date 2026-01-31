package com.vladgad.tablebudgeter

import com.vladgad.tablebudgeter.RepositoryInterface
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class BudgetViewModelUnitTest {
    //проверяем что при запуске транзакци и вернулась 1 как вставленная строка
    private lateinit var viewModel: BudgetViewModel
    private lateinit var repositoryMock: RepositoryInterface

    @Before
    fun setUp() {
        repositoryMock = mock()
        viewModel = BudgetViewModel(repositoryMock)
    }

    @Test
    fun `addTransaction should update transaction list on success`() {
        // Arrange
        val transaction = Transaction(
            operation = "Кафе",
            amount = 500.0,
            account = "Наличные"
        )

        // Настраиваем мок: при вызове insertTransaction возвращаем номер строки 42
        whenever(repositoryMock.insertTransaction(any()))
            .thenReturn(1)

        // Act
        val insetRow = viewModel.addTransaction(transaction)

        // Assert


        // Проверяем, что транзакция сохранилась с правильным rowNumber
        assertEquals(1, insetRow)
//        assertEquals("Кафе", savedTransaction.operation)
//        assertEquals(500.0, savedTransaction.amount)
    }

    @Test
    fun `verify addTransaction should update transaction list on success`() {
        // Arrange
        val transaction = Transaction(
            operation = "Кафе",
            amount = 500.0,
            account = "Наличные"
        )

        // Настраиваем мок: при вызове insertTransaction возвращаем номер строки 42
        whenever(repositoryMock.insertTransaction(any()))
            .thenReturn(1)

        // Act
        val insetRow = viewModel.addTransaction(transaction)
        // Assert
        verify(repositoryMock).insertTransaction(transaction)
    }
}
