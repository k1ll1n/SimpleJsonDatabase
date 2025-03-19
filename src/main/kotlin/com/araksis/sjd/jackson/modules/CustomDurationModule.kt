package com.araksis.sjd.jackson.modules

import com.fasterxml.jackson.databind.module.SimpleModule
import java.time.Duration


class CustomDurationModule : SimpleModule() {
    init {
        addSerializer(Duration::class.java, DurationSerializer())
        addDeserializer(Duration::class.java, DurationDeserializer())
    }
}