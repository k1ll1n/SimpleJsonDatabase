package com.araksis.sjd.core.services

import com.araksis.sjd.core.BaseEntityCollection
import java.util.*

class TransactionService<T : Any>(private val jsonEntityManager: BaseEntityCollection<T>) {
    private val operations = LinkedList<() -> Unit>()
    private var isTransactionActive = false

    fun isTransactionActive() = isTransactionActive

    private fun beginTransaction() {
        if (isTransactionActive) {
            throw IllegalStateException("Transaction is already active")
        }
        isTransactionActive = true
        operations.clear()
    }

    private fun commit() {
        if (!isTransactionActive) {
            throw IllegalStateException("No active transaction to commit")
        }
        try {
            for (operation in operations) {
                operation()
            }
            jsonEntityManager.commitChanges()
            isTransactionActive = false
        } catch (e: Exception) {
            rollback()
            throw TransactionException("Transaction failed and was rolled back", e)
        }
    }

    private fun rollback() {
        if (!isTransactionActive) {
            throw IllegalStateException("No active transaction to rollback")
        }
        operations.clear()
        isTransactionActive = false
    }

    fun <R> runInTransaction(block: () -> R): R {
        beginTransaction()
        try {
            val result = block()
            commit()
            return result
        } catch (e: Exception) {
            rollback()
            throw TransactionException("Transaction failed and was rolled back", e)
        }

    }

    fun addOperation(operation: () -> Unit) {
        if (!isTransactionActive) {
            throw IllegalStateException("Cannot add operation outside of transaction")
        }
        operations.add(operation)
    }
}

class TransactionException(message: String, cause: Throwable? = null) : Exception(message, cause)