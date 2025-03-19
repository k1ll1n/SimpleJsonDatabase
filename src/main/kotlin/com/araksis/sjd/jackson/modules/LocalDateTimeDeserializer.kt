package com.araksis.sjd.jackson.modules

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class LocalDateTimeDeserializer :
    StdDeserializer<LocalDateTime?>(LocalDateTime::class.java) {
    @Throws(IOException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): LocalDateTime {
        val date = p.text
        return LocalDateTime.parse(date, formatter)
    }

    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}