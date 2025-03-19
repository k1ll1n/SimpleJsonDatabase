package com.araksis.sjd.extensions

import java.io.File

fun File.createIfNotExists(): File {
    if (!this.exists()) this.createNewFile()
    return this
}