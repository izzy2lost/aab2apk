package com.aab2apk.original


object Log {

    var showLogs: Boolean = true
    fun i(message: String) {
        if (showLogs) {
            println(message)
        }
    }
}