package com.araksis.sjd.extensions

import com.araksis.sjd.annotations.UniqueKey
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

fun <T : Any> KClass<T>.getUniqueKey(): KProperty1<T, *>? {
    return this.memberProperties.find { it.findAnnotation<UniqueKey>() != null }
}