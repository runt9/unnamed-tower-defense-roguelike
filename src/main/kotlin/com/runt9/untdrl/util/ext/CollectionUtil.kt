package com.runt9.untdrl.util.ext

fun <T : Any> List<T>.removeIf(predicate: (T) -> Boolean): List<T> {
    val mutList = toMutableList()
    mutList.removeIf(predicate)
    return mutList.toList()
}
