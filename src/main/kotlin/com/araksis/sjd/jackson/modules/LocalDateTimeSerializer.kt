package com.araksis.sjd.jackson.modules

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class LocalDateTimeSerializer : StdSerializer<LocalDateTime>(LocalDateTime::class.java) {
    @Throws(IOException::class)
    override fun serialize(value: LocalDateTime, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.format(formatter))
    }

    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}