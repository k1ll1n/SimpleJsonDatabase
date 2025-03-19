package com.araksis.sjd.core

import com.araksis.sjd.annotations.SJDDocument
import com.araksis.sjd.extensions.mkdirIfNotExists
import com.araksis.sjd.jackson.modules.CustomDurationModule
import com.araksis.sjd.jackson.modules.CustomLocalDateTimeModule
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import kotlinx.coroutines.sync.withLock
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

abstract class JsonRepository<T : Any>(private val clazz: KClass<T>, private val sjdConfig: SJDConfig) {
    private var path: String = "${sjdConfig.getCollectionsDirectoryPath()}/${getTableName()}.json"
    private val mapper: ObjectMapper

    init {
        mapper = initObjectMapper()
        createDatabaseDirectories()
        createCollection()
    }

    private fun initObjectMapper(): ObjectMapper {
        return jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(ParameterNamesModule())
            registerModule(CustomLocalDateTimeModule())
            registerModule(CustomDurationModule())
        }
    }

    private fun createDatabaseDirectories() {
        mkdir(sjdConfig.getCollectionsDirectoryPath())
        mkdir(sjdConfig.getBackupsDirectoryPath())
        mkdir(sjdConfig.getLogsDirectoryPath())
    }

    private fun mkdir(path: String) {
        path.mkdirIfNotExists()
    }

    private fun createCollection() {
        if (Files.notExists(Paths.get(path))) {
            File(path).createNewFile()
        }
    }

    private fun getTableName(): String {
        return clazz.findAnnotation<SJDDocument>()?.collectionName
            ?: clazz.simpleName
            ?: throw IllegalArgumentException("Class ${clazz.simpleName} must have either @SJDDocument annotation.")
    }

    protected suspend fun saveRecords(records: List<T>) {
        val tempPath = "$path.tmp"
        try {
            GlobalLock.mutex.withLock {
                BufferedWriter(FileWriter(tempPath)).use { writer ->
                    mapper.writeValue(writer, records)
                }
            }

            val writtenData = readRecords(tempPath)
            if (GlobalLock.mutex.withLock {writtenData.hashCode() != records.hashCode()}) {
                throw IOException("Data inconsistency after writing")
            }

            GlobalLock.mutex.withLock { File(tempPath).renameTo(File(path)) }
        } catch (e: Exception) {
            File(tempPath).delete()
            throw e
        }
    }

    protected suspend fun readRecords(tempPath: String? = null): List<T> {
        return GlobalLock.mutex.withLock {
            try {
                File(tempPath ?: path).bufferedReader().use { reader ->
                    mapper.readValue(reader, mapper.typeFactory.constructCollectionType(List::class.java, clazz.java))
                }
            } catch (e: MismatchedInputException) {
                emptyList()
            } catch (e: IOException) {
                emptyList()
            }
        }
    }
}