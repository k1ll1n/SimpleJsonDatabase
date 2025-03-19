package com.araksis.sjd.jackson.modules

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.time.Duration

class DurationSerializer : StdSerializer<Duration>(Duration::class.java) {
    override fun serialize(value: Duration, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.toString())
    }
}