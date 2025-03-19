package com.araksis.sjd.jackson.modules

import com.fasterxml.jackson.databind.module.SimpleModule
import java.time.LocalDateTime


class CustomLocalDateTimeModule : SimpleModule() {
    init {
        addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer())
        addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer())
    }
}