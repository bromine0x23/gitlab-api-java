/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http.HttpMethod.requiresRequestBody
import java.net.URI

internal class OkHttpHttpRequest(
	private val client: OkHttpClient,
	override val method: HttpMethod,
	override val uri: URI
) : AbstractBufferingHttpRequest() {

	override val methodValue: String
		get() = method.name

	override fun doExecute(headers: HttpHeaders, bodyContent: ByteArray): HttpResponse {
		val request = buildRequest(method, uri, headers, bodyContent)
		val response = client.newCall(request).execute()
		return OkHttpHttpResponse(response)
	}

	private fun buildRequest(method: HttpMethod, uri: URI, headers: HttpHeaders, bodyContent: ByteArray): Request {
		val body = if (bodyContent.isNotEmpty() || requiresRequestBody(method.name)) {
			val contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE)?.toMediaTypeOrNull()
			bodyContent.toRequestBody(contentType)
		} else {
			null
		}
		return Request.Builder().url(uri.toURL()).method(method.name, body).apply {
			headers.forEach { (name, values) ->
				values.forEach { value ->
					addHeader(name, value)
				}
			}
		}.build()
	}
}