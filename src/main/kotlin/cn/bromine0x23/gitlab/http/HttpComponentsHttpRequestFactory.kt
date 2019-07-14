/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.*
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.HttpClients
import org.apache.http.protocol.HttpContext
import java.net.URI
import java.time.Duration

open class HttpComponentsHttpRequestFactory(
	var client: HttpClient = HttpClients.createSystem()
) : HttpRequestFactory {

	private var requestConfig: RequestConfig? = null

	var connectTimeout: Duration
		get() = throw UnsupportedOperationException()
		set(value) {
			requestConfigBuilder().setConnectTimeout(value.toMillis().toInt())
		}

	var connectionRequestTimeout: Duration
		get() = throw UnsupportedOperationException()
		set(value) {
			requestConfigBuilder().setConnectionRequestTimeout(value.toMillis().toInt())
		}

	var socketTimeout: Duration
		get() = throw UnsupportedOperationException()
		set(value) {
			requestConfigBuilder().setSocketTimeout(value.toMillis().toInt())
		}

	override fun build(method: HttpMethod, uri: URI): HttpRequest {
		val client = client
		val request = createHttpUriRequest(method, uri).also { postProcessHttpRequest(it) }
		val context = createHttpContext(method, uri)
		if (context.getAttribute(HttpClientContext.REQUEST_CONFIG) == null) {
			val config: RequestConfig? = request.takeIf { it is Configurable }
				?.let { (it as Configurable).config }
				?: createRequestConfig(client)
			config?.let { context.setAttribute(HttpClientContext.REQUEST_CONFIG, it) }
		}
		return HttpComponentsHttpRequest(client, request, context)
	}
	protected open fun mergeRequestConfig(clientConfig: RequestConfig): RequestConfig {
		val requestConfig = requestConfig ?: return clientConfig
		return RequestConfig.copy(clientConfig).apply {
			requestConfig.connectTimeout.takeIf { it >= 0 }?.let { setConnectTimeout(it) }
			requestConfig.connectionRequestTimeout.takeIf { it >= 0 }?.let { setConnectionRequestTimeout(it) }
			requestConfig.socketTimeout.takeIf { it >= 0 }?.let { setSocketTimeout(it) }
		}.build()
	}

	protected open fun createHttpContext(method: HttpMethod, uri: URI): HttpContext = HttpClientContext.create()

	protected open fun postProcessHttpRequest(request: HttpUriRequest) {
		// do nothing
	}

	private fun createHttpUriRequest(method: HttpMethod, uri: URI): HttpUriRequest = when (method) {
		HttpMethod.GET     -> HttpGet(uri)
		HttpMethod.HEAD    -> HttpHead(uri)
		HttpMethod.POST    -> HttpPost(uri)
		HttpMethod.PUT     -> HttpPut(uri)
		HttpMethod.PATCH   -> HttpPatch(uri)
		HttpMethod.DELETE  -> HttpDelete(uri)
		HttpMethod.OPTIONS -> HttpOptions(uri)
		HttpMethod.TRACE   -> HttpTrace(uri)
	}

	private fun createRequestConfig(client: HttpClient) = if (client is Configurable) {
		mergeRequestConfig(client.config)
	} else {
		this.requestConfig
	}

	private fun requestConfigBuilder() = if (requestConfig != null) {
		RequestConfig.copy(requestConfig)
	} else {
		RequestConfig.custom()
	}
}