package com.example.scookie.logger

interface Logger {
    fun log(msg: String)
    fun error(msg: String)
    fun info(msg: String)
}