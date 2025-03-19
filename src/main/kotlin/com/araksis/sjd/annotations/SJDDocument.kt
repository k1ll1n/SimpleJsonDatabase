package com.araksis.sjd.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SJDDocument(val collectionName: String)
