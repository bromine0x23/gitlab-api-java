/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

import okhttp3.internal.EMPTY_BYTE_ARRAY
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import java.io.ByteArrayInputStream
import java.io.Closeable
import java.io.IOException
import java.io.InputStream

internal class HttpComponentsHttpResponse(private val response: HttpResponse) : AbstractHttpResponse() {

	private var _headers: HttpHeaders? = null

	override val status: Int
		get() = response.statusLine.statusCode

	override val headers: HttpHeaders
		get() {
			var headers = _headers
			if (headers == null) {
				headers = HttpHeaders().apply { response.allHeaders.forEach { this += it.name to it.value } }
				this._headers = headers
			}
			return headers
		}

	override val body: InputStream
		get() = response.entity?.content ?: ByteArrayInputStream(EMPTY_BYTE_ARRAY)

	override fun close() {
		try {
			try {
				EntityUtils.consume(response.entity)
			} finally {
				if (response is Closeable) {
					response.close()
				}
			}
		} catch (exception: IOException) {
			// ignore
		}
	}
}