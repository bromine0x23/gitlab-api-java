/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

import java.net.HttpURLConnection
import java.net.URI

internal class SimpleBufferingHttpRequest(
	private val connection: HttpURLConnection
) : AbstractBufferingHttpRequest() {

	override val methodValue: String
		get() = connection.requestMethod

	override val uri: URI
		get() = connection.url.toURI()

	override fun doExecute(headers: HttpHeaders, bodyContent: ByteArray): HttpResponse {
		addHeaders(connection, headers)
		if (connection.doOutput) {
			connection.setFixedLengthStreamingMode(bodyContent.size)
		}
		connection.connect()
		if (connection.doOutput) {
			connection.outputStream.apply { write(bodyContent) }
		} else {
			connection.responseCode
		}
		return SimpleHttpResponse(connection)
	}

	companion object {
		@JvmStatic
		internal fun addHeaders(connection: HttpURLConnection, headers: HttpHeaders) {
			headers.forEach { (name, values) -> addHeaders(connection, name, values) }
		}

		@JvmStatic
		private fun addHeaders(connection: HttpURLConnection, name: String, values: List<String>) {
			if (HttpHeaders.COOKIE.equals(name, true)) {
				connection.setRequestProperty(name, values.joinToString("; "))
			} else {
				values.forEach { value -> connection.addRequestProperty(name, value) }
			}
		}
	}
}