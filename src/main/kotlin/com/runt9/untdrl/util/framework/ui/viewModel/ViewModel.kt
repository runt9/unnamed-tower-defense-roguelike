package com.runt9.untdrl.util.framework.ui.viewModel

import com.badlogic.gdx.utils.Disposable
import com.runt9.untdrl.util.framework.ui.Updatable
import com.runt9.untdrl.util.framework.ui.UpdatableValue
import com.runt9.untdrl.util.framework.ui.updatable
import com.runt9.untdrl.util.framework.ui.updatableValue

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

    open inner class Binding<T : Any>(initialValue: T) : Disposable {
        internal var dirty = false
        protected var savedValue = initialValue
        protected var currentValue = savedValue
        protected val binds = mutableSetOf<Updatable>()

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

    inner class ListBinding<T : Any>(initialValue: List<T> = emptyList()) : Binding<List<T>>(initialValue) {
        private val addBinds = mutableSetOf<UpdatableValue<T>>()
        private val removeBinds = mutableSetOf<UpdatableValue<T>>()

        fun bindAdd(updateFn: UpdatableValue<T>.(T) -> Unit) {
            val updatable = updatableValue(updateFn)
            addBinds.add(updatable)
            currentValue.forEach(updatable::update)
        }

        fun bindRemove(updateFn: UpdatableValue<T>.(T) -> Unit) {
            val updatable = updatableValue(updateFn)
            removeBinds.add(updatable)
        }

        fun add(value: T) {
            set(currentValue + value)
            addBinds.forEach { u -> u.update(value) }
        }

        fun remove(value: T) {
            set(currentValue - value)
            removeBinds.forEach { u -> u.update(value) }
        }

        operator fun plusAssign(toAdd: T) {
            add(toAdd)
        }

        operator fun plusAssign(toAdd: Collection<T>) {
            toAdd.forEach(::add)
        }

        operator fun minusAssign(toRemove: T) {
            remove(toRemove)
        }

        fun removeIf(predicate: (T) -> Boolean) {
            get().find(predicate)?.also(::remove)
        }
    }
}

fun emptyViewModel() = object : ViewModel() {}
