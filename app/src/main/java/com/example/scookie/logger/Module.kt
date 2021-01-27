package com.example.scookie.logger

import org.koin.dsl.module

val loggerModule = module {
    single {  LogcatLogger("ScookieApp") as Logger }
}