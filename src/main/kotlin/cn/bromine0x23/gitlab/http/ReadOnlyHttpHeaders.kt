/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

class ReadOnlyHttpHeaders(headers: HttpHeaders) : HttpHeaders(headers.headers) {

	override operator fun set(name: String, value: String) {
		throw UnsupportedOperationException()
	}

	override operator fun set(name: String, value: List<String>) {
		throw UnsupportedOperationException()
	}

	override operator fun plusAssign(pair: Pair<String, String>) {
		throw UnsupportedOperationException()
	}

}