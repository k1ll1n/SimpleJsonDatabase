package com.araksis.sjd.models

import com.araksis.sjd.annotations.UniqueKey
import java.util.*

abstract class SJDCollection {
    @UniqueKey
    val id: UUID = UUID.randomUUID()
}