package com.araksis.sjd.core.services

import com.araksis.sjd.core.GlobalLock
import com.araksis.sjd.core.SJDConfig
import com.araksis.sjd.extensions.createIfNotExists
import com.araksis.sjd.extensions.toTimestamp
import com.araksis.sjd.extensions.getUniqueKey
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.time.LocalDateTime
import kotlin.reflect.KClass

class LogService<T : Any>(private val sjdConfig: SJDConfig, private val clazz: KClass<T>) {
    private val timestamp = LocalDateTime.now().toTimestamp("dd-MM-yyyy")
    private val logFilePath = "${sjdConfig.getLogsDirectoryPath()}/${timestamp}_change_log.txt"
    private val logFile: File

    init {
        logFile = createLogFile()
    }

    private fun createLogFile(): File {
        return File(logFilePath).createIfNotExists()
    }

    fun logChange(operation: String, entity: T) = runBlocking {
        GlobalLock.mutex.withLock {
            val timestamp = LocalDateTime.now().toTimestamp()
            val entityId = getEntityId(entity)
            val entityType = entity::class.simpleName
            val logEntry = "[$timestamp] $operation on $entityType with ID $entityId\n"

            try {
                logFile.appendText(logEntry)
            } catch (e: Exception) {
                println("Failed to write to change log: ${e.message}")
            }
        }
    }

    private fun getEntityId(entity: T): String {
        return clazz.getUniqueKey()?.get(entity).toString()
    }
}