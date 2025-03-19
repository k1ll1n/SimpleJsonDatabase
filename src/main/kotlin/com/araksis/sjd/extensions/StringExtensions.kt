package com.araksis.sjd.extensions

import java.nio.file.Files
import java.nio.file.Paths

fun String.mkdirIfNotExists() {
    if (Files.notExists(Paths.get(this))) {
        Files.createDirectories(Paths.get(this))
    }
}