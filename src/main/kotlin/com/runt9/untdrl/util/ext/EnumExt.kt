package com.runt9.untdrl.util.ext

inline fun <reified E : Enum<E>> Int.matchOrdinal() = enumValues<E>().find { it.ordinal == this }
