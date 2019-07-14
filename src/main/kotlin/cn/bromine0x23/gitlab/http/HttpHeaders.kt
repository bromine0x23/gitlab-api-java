/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

import cn.bromine0x23.gitlab.utility.CaseInsensitiveMap
import java.util.*

open class HttpHeaders : MutableMap<String, MutableList<String>> {

	companion object {

		@JvmStatic val CONTENT_LENGTH    = "Content-Length"
		@JvmStatic val CONTENT_TYPE      = "Content-Type"
		@JvmStatic val COOKIE            = "Cookie"
		@JvmStatic val TRANSFER_ENCODING = "Transfer-Encoding"

		@JvmStatic
		private val LOCALE = Locale.ENGLISH
	}

	internal val headers: MutableMap<String, MutableList<String>> = CaseInsensitiveMap(mutableMapOf(),  LOCALE)

	constructor()

	constructor(vararg pairs: Pair<String, List<String>>) {
		pairs.forEach { (name, value) -> this.headers[name] = value.toMutableList() }
	}

	constructor(headers: Map<String, List<String>>) : this() {
		headers.forEach { (key, value) -> this.headers[key] = value.toMutableList() }
	}

	open operator fun set(name: String, value: String) {
		this.headers[name] = mutableListOf(value)
	}

	open operator fun set(name: String, value: List<String>) {
		this.headers[name] = value.toMutableList()
	}

	open fun getFirst(name: String): String? {
		if (headers.contains(name)) {
			return headers.getValue(name).firstOrNull()
		}
		return null
	}

	open operator fun plusAssign(pair: Pair<String, String>) {
		val (name, value) = pair
		if (headers.contains(name)) {
			this.headers.getValue(name) += value
		} else {
			this.headers[name] = mutableListOf(value)
		}
	}

	var contentLength: Int
		get() = getFirst(CONTENT_LENGTH)?.toInt() ?: -1
		set(value) = set(CONTENT_LENGTH, value.toString())

	override val size: Int get() = headers.size
	override val entries: MutableSet<MutableMap.MutableEntry<String, MutableList<String>>> get() = headers.entries
	override val keys: MutableSet<String> get() = headers.keys
	override val values: MutableCollection<MutableList<String>> get() = headers.values

	override fun containsKey(key: String): Boolean = headers.containsKey(key)
	override fun containsValue(value: MutableList<String>): Boolean = headers.containsValue(value)
	override fun get(key: String): MutableList<String>? = headers[key]
	override fun isEmpty(): Boolean = headers.isEmpty()
	override fun clear() = headers.clear()
	override fun put(key: String, value: MutableList<String>): MutableList<String>? = headers.put(key, value)
	override fun putAll(from: Map<out String, MutableList<String>>) = headers.putAll(from)
	override fun remove(key: String): MutableList<String>? = headers.remove(key)
}

