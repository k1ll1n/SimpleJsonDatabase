package com.araksis.sjd.core

class SJDConfig(
    private val basePath: String,
    val backupIntervalHours: Long = 24,
    val cacheInterval: Long = 5000L
) {
    private fun getBaseDirectoryPath(): String {
        return if (basePath.isBlank()) "./sjd" else "$basePath/sjd"
    }

    fun getCollectionsDirectoryPath(): String {
        return "${getBaseDirectoryPath()}/collections"
    }

    fun getBackupsDirectoryPath(): String {
        return "${getBaseDirectoryPath()}/backups"
    }

    fun getLogsDirectoryPath(): String {
        return "${getBaseDirectoryPath()}/logs"
    }
}