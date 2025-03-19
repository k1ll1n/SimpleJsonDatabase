package com.araksis.sjd.core

import com.araksis.sjd.core.services.BackupService
import com.araksis.sjd.core.services.TransactionService
import kotlinx.coroutines.*
import kotlin.reflect.KClass

class JsonEntityCollection<T : Any>(private val clazz: KClass<T>, private val sjdConfig: SJDConfig) : BaseEntityCollection<T>(clazz, sjdConfig) {
    private val backupService = BackupService(sjdConfig)
    private val transactionService: TransactionService<T> = TransactionService(this)

    fun insert(entity: T) = runBlocking {
        if (transactionService.isTransactionActive()) {
            transactionService.addOperation { internalInsert(entity) }
        } else {
            internalInsert(entity)
        }
    }

    fun update(entity: T) = runBlocking {
        if (transactionService.isTransactionActive()) {
            transactionService.addOperation { internalUpdate(entity) }
        } else {
            internalUpdate(entity)
        }
    }

    fun delete(entity: T) = runBlocking {
        if (transactionService.isTransactionActive()) {
            transactionService.addOperation { internalDelete(entity) }
        } else {
            internalDelete(entity)
        }
    }

    fun findAll(): List<T> = internalFindAll()

    fun findBy(filter: (T) -> Boolean): List<T> = internalFindBy(filter)

    fun isExist(filter: (T) -> Boolean): Boolean = internalIsExist(filter)

    fun <R> runInTransaction(block: () -> R): R = transactionService.runInTransaction(block)

    fun createBackup(): Boolean = backupService.createBackup()

    fun restoreBackup(timestamp: Long): Boolean = backupService.restoreBackup(timestamp)

    fun listBackups(): List<Long> = backupService.listBackups()
}
