/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

import org.apache.http.HttpEntityEnclosingRequest
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.protocol.HttpContext
import java.net.URI

class HttpComponentsHttpRequest(
	private val client: HttpClient,
	private val request: HttpUriRequest,
	private val context: HttpContext
) : AbstractBufferingHttpRequest() {

	override val methodValue: String
		get() = request.method

	override val uri: URI
		get() = request.uri

	override fun doExecute(headers: HttpHeaders, bodyContent: ByteArray): HttpResponse {
		addHeaders(request, headers)
		if (request is HttpEntityEnclosingRequest) {
			request.entity = ByteArrayEntity(bodyContent)
		}
		val response = client.execute(request, context)
		return HttpComponentsHttpResponse(response)
	}

	companion object {
		@JvmStatic
		internal fun addHeaders(request: HttpUriRequest, headers: HttpHeaders) {
			headers.forEach { (name, values) -> addHeaders(request, name, values) }
		}

		@JvmStatic
		private fun addHeaders(request: HttpUriRequest, name: String, values: List<String>) {
			if (HttpHeaders.COOKIE.equals(name, true)) {
				request.addHeader(name, values.joinToString("; "))
			} else if (!(HttpHeaders.CONTENT_LENGTH.equals(name, true) || HttpHeaders.TRANSFER_ENCODING.equals(name, true))) {
				values.forEach { value -> request.addHeader(name, value) }
			}
		}
	}
}