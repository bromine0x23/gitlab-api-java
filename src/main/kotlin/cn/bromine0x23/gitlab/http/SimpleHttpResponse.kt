/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

import java.io.InputStream
import java.net.HttpURLConnection

internal class SimpleHttpResponse(private val connection: HttpURLConnection) : AbstractHttpResponse() {

	private var _headers: HttpHeaders? = null

	private var _body: InputStream? = null

	override val status: Int
		get() = connection.responseCode

	override val headers: HttpHeaders
		get() {
			var headers = _headers
			if (headers == null) {
				headers = HttpHeaders().apply {
					var i = 0
					while (true) {
						val name = connection.getHeaderFieldKey(i)
						if (name == null || name.isBlank()) {
							break
						}
						plusAssign(name to connection.getHeaderField(i))
						++i
					}
				}
				this._headers = headers
			}
			return headers
		}

	override val body: InputStream
		get() {
			this._body = connection.errorStream ?: connection.inputStream
			return _body as InputStream
		}

	override fun close() {
		try {
			if (_body == null) {
				this.body
			}
			ByteArray(DEFAULT_BUFFER_SIZE).run {
				while (_body!!.read(this) != -1) {
				}
			}
			_body!!.close()
		} catch (exception: Exception) {
			// ignore
		}
	}
}