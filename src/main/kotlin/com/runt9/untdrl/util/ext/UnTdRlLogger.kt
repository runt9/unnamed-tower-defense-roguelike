package com.runt9.untdrl.util.ext

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import ktx.log.Logger

class UnTdRlLogger(name: String) : Logger(name) {
    override fun buildMessage(message: String): String {
        val dt = DateFormat("yyyy-MM-dd HH:mm:ss").format(DateTimeTz.nowLocal())
        val caller = Thread.currentThread().stackTrace[2]
        return "[ $dt | ${Thread.currentThread().name} | ${name}.${caller.methodName} ]: $message"
    }
}

fun unTdRlLogger(): UnTdRlLogger {
    val caller = Thread.currentThread().stackTrace[2]
    return UnTdRlLogger(Class.forName(caller.className).simpleName)
}
