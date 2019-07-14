/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

import okhttp3.Response
import okhttp3.internal.EMPTY_BYTE_ARRAY
import java.io.ByteArrayInputStream
import java.io.InputStream

internal class OkHttpHttpResponse(private val response: Response) : AbstractHttpResponse() {

	private var _headers: HttpHeaders? = null

	override val status: Int
		get() = response.code

	override val headers: HttpHeaders
		get() {
			var headers = _headers
			if (headers == null) {
				headers = HttpHeaders().apply { response.headers.forEach(this::plusAssign) }
				this._headers = headers
			}
			return headers
		}

	override val body: InputStream
		get() = response.body?.byteStream() ?: ByteArrayInputStream(EMPTY_BYTE_ARRAY)

	override fun close() {
		response.body?.close()
	}
}