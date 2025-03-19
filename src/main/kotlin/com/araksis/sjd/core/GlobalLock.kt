package com.araksis.sjd.core

import kotlinx.coroutines.sync.Mutex

object GlobalLock {
    val mutex = Mutex()
}