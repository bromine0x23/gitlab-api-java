/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URI
import java.net.URL
import java.time.Duration

class SimpleHttpRequestFactory : HttpRequestFactory {

	var proxy: Proxy? = null

	var connectTimeout: Duration? = null

	var readTimeout: Duration? = null

	override fun build(method: HttpMethod, uri: URI): HttpRequest {
		val connection = openConnection(uri.toURL(), proxy)
		prepareConnection(connection, method)

		return SimpleBufferingHttpRequest(connection)
	}

	protected fun openConnection(url: URL, proxy: Proxy?): HttpURLConnection {
		val connection = when (proxy) {
			null -> url.openConnection()
			else -> url.openConnection(proxy)
		}
		if (connection !is HttpURLConnection) {
			throw IllegalStateException("HttpURLConnection required for [$url] but got: $connection")
		}
		return connection
	}

	protected fun prepareConnection(connection: HttpURLConnection, method: HttpMethod) {
		if (connectTimeout != null) {
			connection.connectTimeout = connectTimeout!!.toMillis().toInt()
		}
		if (readTimeout != null) {
			connection.readTimeout = readTimeout!!.toMillis().toInt()
		}

		connection.doInput = true
		connection.instanceFollowRedirects = method == HttpMethod.GET
		connection.doOutput = when (method) {
			HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE -> true
			else -> false
		}
		connection.requestMethod = method.name
	}
}