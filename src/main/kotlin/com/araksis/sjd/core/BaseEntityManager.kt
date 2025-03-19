package com.araksis.sjd.core

import com.araksis.sjd.core.services.LogService
import com.araksis.sjd.extensions.getUniqueKey
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

open class BaseEntityManager<T : Any>(private val clazz: KClass<T>, private val sjdConfig: SJDConfig) : JsonRepository<T>(clazz, sjdConfig) {
    private var cachedRecords: List<T> = mutableListOf()
    private var hash: Int = 0
    private var lastSaveTime = System.currentTimeMillis()
    private val logService = LogService(sjdConfig, clazz)

    init {
        runBlocking {
            cachedRecords = readRecords().toMutableList()
            hash = cachedRecords.hashCode()
        }

        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(1000L)
                autoSave()
            }
        }
    }

    protected fun internalInsert(entity: T) = runBlocking {
        GlobalLock.mutex.withLock {
            cachedRecords = cachedRecords + entity
        }
        logService.logChange("INSERT", entity)
    }

    protected fun internalUpdate(entity: T) = runBlocking {
        GlobalLock.mutex.withLock {
            cachedRecords = cachedRecords.map { if (isSameEntity(it, entity)) entity else it }
        }
        logService.logChange("UPDATE", entity)
    }

    protected fun internalDelete(entity: T) = runBlocking {
        GlobalLock.mutex.withLock {
            cachedRecords = cachedRecords.filter { !isSameEntity(it, entity) }
        }
        logService.logChange("DELETE", entity)
    }

    protected fun internalFindAll(): List<T> {
        return cachedRecords
    }

    protected fun internalFindBy(filter: (T) -> Boolean): List<T> {
        return cachedRecords.filter {filter(it)}
    }

    protected fun internalIsExist(filter: (T) -> Boolean): Boolean {
        return internalFindBy(filter).isNotEmpty()
    }

    private fun isSameEntity(entity1: T, entity2: T): Boolean {
        return clazz.getUniqueKey()?.let { it.get(entity1) == it.get(entity2) } ?: false
    }

    fun commitChanges() = runBlocking {
        if (GlobalLock.mutex.withLock { cachedRecords.hashCode() != hash }) {
            saveRecords(cachedRecords)
            hash = cachedRecords.hashCode()

            return@runBlocking true
        }

        return@runBlocking false
    }

    private fun autoSave() {
        if (System.currentTimeMillis() - lastSaveTime > sjdConfig.cacheInterval) {
            commitChanges()
            lastSaveTime = System.currentTimeMillis()
        }
    }
}