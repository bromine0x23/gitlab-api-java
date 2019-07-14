/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

import java.io.ByteArrayOutputStream
import java.io.OutputStream

abstract class AbstractBufferingHttpRequest : AbstractHttpRequest() {

	private var bufferedOutput: ByteArrayOutputStream = ByteArrayOutputStream(1024)

	override fun doGetBody(headers: HttpHeaders): OutputStream {
		return bufferedOutput
	}

	override fun doExecute(headers: HttpHeaders): HttpResponse {
		val bodyContent = bufferedOutput.toByteArray() as ByteArray
		if (headers.contentLength < 0) {
			headers.contentLength = bodyContent.size
		}
		val response = doExecute(headers, bodyContent)
		bufferedOutput = ByteArrayOutputStream(0)
		return response
	}

	protected abstract fun doExecute(headers: HttpHeaders, bodyContent: ByteArray): HttpResponse
}