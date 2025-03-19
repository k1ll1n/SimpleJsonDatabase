package com.araksis.sjd.jackson.modules

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.time.Duration

class DurationDeserializer : StdDeserializer<Duration?>(Duration::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Duration {
        val durationString = p.text.trim()
        return Duration.parse(durationString)
    }
}