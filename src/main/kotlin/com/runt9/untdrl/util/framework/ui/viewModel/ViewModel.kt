package com.runt9.untdrl.util.framework.ui.viewModel

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.util.framework.ui.Updatable
import com.runt9.untdrl.util.framework.ui.updatable

abstract class ViewModel : Disposable {
    private val fields = mutableListOf<Binding<*>>()
    val dirty = Binding(false)

    override fun dispose() {
        fields.forEach(Disposable::dispose)
        dirty(false)
    }

    private fun evaluateDirty() {
        dirty(fields.filter { it != dirty }.any { it.dirty })
    }

    fun saveCurrent() {
        fields.forEach(Binding<*>::saveCurrent)
        dirty(false)
    }

    inner class Binding<T : Any>(initialValue: T) : Disposable {
        internal var dirty = false
        private var savedValue = initialValue
        private var currentValue = savedValue
        private val binds = mutableSetOf<Updatable>()

        init {
            fields.add(this)
        }

        operator fun invoke(value: T) = set(value)

        fun set(value: T) {
            if (value == currentValue) return
            currentValue = value
            binds.forEach(Updatable::update)
            dirty = currentValue != savedValue
            evaluateDirty()
        }

        fun get() = currentValue

        fun bind(updatable: Updatable) = binds.add(updatable)
        fun bind(updateFn: Updatable.() -> Unit) {
            val updatable = updatable(updateFn)
            bind(updatable)
            updatable.update()
        }

        fun saveCurrent() {
            savedValue = currentValue
            dirty = false
        }

        override fun dispose() {
            binds.clear()
            currentValue = savedValue
            dirty = false
        }
    }
}

fun emptyViewModel() = object : ViewModel() {}

operator fun <T : Any> ViewModel.Binding<List<T>>.plusAssign(toAdd: T) {
    val newList = get() + toAdd
    set(newList)
}

operator fun <T : Any> ViewModel.Binding<List<T>>.minusAssign(toRemove: T) {
    val newList = get() - toRemove
    set(newList)
}

fun <T : Any> ViewModel.Binding<List<T>>.removeIf(removeFilter: (T) -> Boolean) {
    val newList = get().filterNot(removeFilter)
    set(newList)
}
