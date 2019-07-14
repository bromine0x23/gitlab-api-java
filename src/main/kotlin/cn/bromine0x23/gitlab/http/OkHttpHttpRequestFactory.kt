/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

import okhttp3.OkHttpClient
import java.net.URI
import java.time.Duration

class OkHttpHttpRequestFactory(
	private var client: OkHttpClient
) : HttpRequestFactory {

	var connectTimeout: Duration
		get() = Duration.ofMillis(client.connectTimeoutMillis.toLong())
		set(value) {
			client = client.newBuilder().connectTimeout(value).build()
		}

	var readTimeout: Duration
		get() = Duration.ofMillis(client.readTimeoutMillis.toLong())
		set(value) {
			client = client.newBuilder().readTimeout(value).build()
		}

	var writeTimeout: Duration
		get() = Duration.ofMillis(client.writeTimeoutMillis.toLong())
		set(value) {
			client = client.newBuilder().writeTimeout(value).build()
		}

	override fun build(method: HttpMethod, uri: URI): HttpRequest = OkHttpHttpRequest(client, method, uri)

}