package com.araksis.sjd.core.services

import com.araksis.sjd.core.GlobalLock
import com.araksis.sjd.core.SJDConfig
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.withLock

class BackupService(private val sjdConfig: SJDConfig) {
    private val backupDir = sjdConfig.getBackupsDirectoryPath()

    init {
        startScheduledBackups()
    }

    private fun startScheduledBackups() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(sjdConfig.backupIntervalHours * 60 * 60 * 1000L) // Интервал в миллисекундах
                createBackup()
            }
        }
    }

    fun createBackup(): Boolean = runBlocking {
        return@runBlocking GlobalLock.mutex.withLock {
            val timestamp = System.currentTimeMillis()
            val backupPath = "$backupDir/backup_$timestamp"
            val backupFile = File(backupPath)

            try {
                File(sjdConfig.getCollectionsDirectoryPath()).copyRecursively(backupFile, overwrite = true)
                println("Backup created successfully at: $backupPath")
                return@withLock true
            } catch (e: Exception) {
                println("Failed to create backup: ${e.message}")
                return@withLock false
            }
        }
    }

    fun restoreBackup(timestamp: Long): Boolean = runBlocking {
        return@runBlocking GlobalLock.mutex.withLock {
            val backupPath = "$backupDir/backup_$timestamp"
            val backupFile = File(backupPath)

            if (backupFile.exists()) {
                try {
                    File(backupPath).copyRecursively(File(sjdConfig.getCollectionsDirectoryPath()), overwrite = true)
                    println("Backup restored successfully from: $backupPath")
                    return@withLock true
                } catch (e: Exception) {
                    println("Failed to restore backup: ${e.message}")
                    return@withLock false
                }
            } else {
                println("Backup with timestamp $timestamp not found")
                return@withLock false
            }
        }
    }

    fun listBackups(): List<Long> = runBlocking {
        return@runBlocking GlobalLock.mutex.withLock {
            return@withLock File(backupDir).listFiles()
                ?.filter { it.isDirectory }
                ?.map { it.name.split("_").last() }
                ?.map { it.toLong() }
                ?: emptyList()
        }
    }
}