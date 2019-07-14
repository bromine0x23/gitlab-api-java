/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.utility

import java.util.*

class CaseInsensitiveMap<TValue>(
	private val target: MutableMap<String, TValue>,
	private val locale: Locale = Locale.getDefault()
) : MutableMap<String, TValue> {

	private val caseInsensitiveKeys: MutableMap<String, String> = mutableMapOf()

	private var _entries: MutableSet<MutableMap.MutableEntry<String, TValue>>? = null

	private var _keys: MutableSet<String>? = null

	private var _values: MutableCollection<TValue>? = null

	override val size: Int
		get() = target.size

	override fun containsKey(key: String): Boolean = caseInsensitiveKeys.containsKey(convertKey(key))

	override fun containsValue(value: TValue): Boolean = target.containsValue(value)

	override fun get(key: String): TValue? {
		val caseInsensitiveKey = caseInsensitiveKeys[convertKey(key)]
		if (caseInsensitiveKey != null) {
			return target[caseInsensitiveKey]
		}
		return null
	}

	override fun isEmpty(): Boolean = target.isEmpty()

	override val entries: MutableSet<MutableMap.MutableEntry<String, TValue>>
		get() {
			var entries = _entries
			if (entries == null) {
				entries = Entries(target.entries)
				this._entries = entries
			}
			return entries
		}

	override val keys: MutableSet<String>
		get() {
			var keys = _keys
			if (keys == null) {
				keys = Keys(target.keys)
				this._keys = keys
			}
			return keys
		}

	override val values: MutableCollection<TValue>
		get() {
			var values = _values
			if (values == null) {
				values = Values(target.values)
				this._values = values
			}
			return values
		}

	override fun put(key: String, value: TValue): TValue? {
		val oldKey = caseInsensitiveKeys.put(convertKey(key), key)
		val oldKeyValue: TValue? = when {
			oldKey != null && oldKey != key -> target.remove(oldKey)
			else -> null
		}
		val oldValue = target.put(key, value)
		return oldKeyValue ?: oldValue
	}

	override fun remove(key: String): TValue? {
		val caseInsensitiveKey = removeCaseInsensitiveKey(key)
		if (caseInsensitiveKey != null) {
			return target.remove(caseInsensitiveKey)
		}
		return null
	}

	override fun putAll(from: Map<out String, TValue>) {
		if (from.isNotEmpty()) {
			from.forEach { (key, value) -> this[key] = value }
		}
	}

	override fun clear() {
		caseInsensitiveKeys.clear()
		target.clear()
	}

	private fun convertKey(key: String): String = key.toLowerCase(locale)

	private fun removeCaseInsensitiveKey(key: String): String? = caseInsensitiveKeys.remove(convertKey(key))

	private inner class Keys(private val delegate: MutableSet<String>) : MutableSet<String> by delegate {

		override fun iterator(): MutableIterator<String> = KeysIterator()

		override fun remove(element: String): Boolean = this@CaseInsensitiveMap.remove(element) != null

		override fun clear() = this@CaseInsensitiveMap.clear()
	}

	private inner class Values(private val delegate: MutableCollection<TValue>) :
		MutableCollection<TValue> by delegate {

		override fun iterator(): MutableIterator<TValue> = ValuesIterator()

		override fun clear() = this@CaseInsensitiveMap.clear()
	}

	private inner class Entries(private val delegate: MutableSet<MutableMap.MutableEntry<String, TValue>>) :
		MutableSet<MutableMap.MutableEntry<String, TValue>> by delegate {

		override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, TValue>> = EntriesIterator()

		override fun remove(element: MutableMap.MutableEntry<String, TValue>): Boolean {
			if (delegate.remove(element)) {
				removeCaseInsensitiveKey(element.key)
				return true
			}
			return false
		}

		override fun clear() {
			caseInsensitiveKeys.clear()
			delegate.clear()
		}
	}

	private abstract inner class EntryIterator<TElement> : MutableIterator<TElement> {

		private val delegate = this@CaseInsensitiveMap.target.entries.iterator()

		private var last: MutableMap.MutableEntry<String, TValue>? = null

		override fun hasNext(): Boolean = delegate.hasNext()

		override fun remove() {
			delegate.remove()
			if (last != null) {
				removeCaseInsensitiveKey(this.last!!.key)
				this.last = null
			}
		}

		protected fun nextEntry(): MutableMap.MutableEntry<String, TValue> {
			val entry = delegate.next()
			this.last = entry
			return entry
		}
	}

	private inner class KeysIterator : EntryIterator<String>() {
		override fun next(): String = nextEntry().key
	}

	private inner class ValuesIterator : EntryIterator<TValue>() {
		override fun next(): TValue = nextEntry().value
	}

	private inner class EntriesIterator : EntryIterator<MutableMap.MutableEntry<String, TValue>>() {
		override fun next(): MutableMap.MutableEntry<String, TValue> = nextEntry()
	}
}