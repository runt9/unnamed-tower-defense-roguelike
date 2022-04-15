package com.runt9.untdrl.util.ext

import com.runt9.untdrl.config.Injector
import ktx.reflect.ReflectedClass
import kotlin.reflect.KClass

inline fun <reified Type : Any> inject(): Type = Injector.inject()
inline fun <reified Type : Any> lazyInject() = lazy { inject<Type>() }

@Suppress("UNCHECKED_CAST")
fun <Type : Any> Injector.newInstanceOf(clazz: KClass<Type>): Type {
    val constructor = ReflectedClass(clazz.java).constructor
    val parameters = constructor.parameterTypes.map { getProvider(it).invoke() }.toTypedArray()
    return constructor.newInstance(*parameters) as Type
}
