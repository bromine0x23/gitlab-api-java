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

	override fun get(key: String): TValue? = caseInsensitiveKeys[convertKey(key)]?.let { target[it] }

	override fun isEmpty(): Boolean = target.isEmpty()

	override val entries: MutableSet<MutableMap.MutableEntry<String, TValue>>
		get() = _entries ?: Entries(target.entries).also { this._entries = it }

	override val keys: MutableSet<String>
		get() = _keys ?: Keys(target.keys).also { this._keys = it }

	override val values: MutableCollection<TValue>
		get() = _values ?: Values(target.values).also { this._values = it }

	override fun put(key: String, value: TValue): TValue? {
		val oldKey = caseInsensitiveKeys.put(convertKey(key), key)
		val oldKeyValue = oldKey?.takeIf { it != key }.let { target.remove(it) }
		val oldValue = target.put(key, value)
		return oldKeyValue ?: oldValue
	}

	override fun remove(key: String): TValue? = removeCaseInsensitiveKey(key)?.let { target.remove(it) }

	override fun putAll(from: Map<out String, TValue>) {
		if (from.isNotEmpty()) {
			from.forEach { (key, value) -> this[key] = value }
		}
	}

	override fun clear() {
		caseInsensitiveKeys.clear()
		target.clear()
	}

	private fun convertKey(key: String) = key.toLowerCase(locale)

	private fun removeCaseInsensitiveKey(key: String) = caseInsensitiveKeys.remove(convertKey(key))

	private inner class Keys(private val delegate: MutableSet<String>) : MutableSet<String> by delegate {

		override fun iterator() = KeysIterator()

		override fun remove(element: String) = this@CaseInsensitiveMap.remove(element) != null

		override fun clear() = this@CaseInsensitiveMap.clear()
	}

	private inner class Values(private val delegate: MutableCollection<TValue>) :
		MutableCollection<TValue> by delegate {

		override fun iterator() = ValuesIterator()

		override fun clear() = this@CaseInsensitiveMap.clear()
	}

	private inner class Entries(private val delegate: MutableSet<MutableMap.MutableEntry<String, TValue>>) :
		MutableSet<MutableMap.MutableEntry<String, TValue>> by delegate {

		override fun iterator() = EntriesIterator()

		override fun remove(element: MutableMap.MutableEntry<String, TValue>) =
			delegate.remove(element) && true.also { removeCaseInsensitiveKey(element.key) }

		override fun clear() {
			caseInsensitiveKeys.clear()
			delegate.clear()
		}
	}

	private abstract inner class EntryIterator<TElement> : MutableIterator<TElement> {

		private val delegate = this@CaseInsensitiveMap.target.entries.iterator()

		private var last: MutableMap.MutableEntry<String, TValue>? = null

		override fun hasNext() = delegate.hasNext()

		override fun remove() {
			delegate.remove()
			if (last != null) {
				removeCaseInsensitiveKey(this.last!!.key)
				this.last = null
			}
		}

		protected fun nextEntry() = delegate.next().also { this.last = it }
	}

	private inner class KeysIterator : EntryIterator<String>() {
		override fun next() = nextEntry().key
	}

	private inner class ValuesIterator : EntryIterator<TValue>() {
		override fun next() = nextEntry().value
	}

	private inner class EntriesIterator : EntryIterator<MutableMap.MutableEntry<String, TValue>>() {
		override fun next() = nextEntry()
	}
}