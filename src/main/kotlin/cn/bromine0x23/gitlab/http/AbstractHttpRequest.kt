/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

import java.io.OutputStream

abstract class AbstractHttpRequest : HttpRequest {

	private var executed: Boolean = false

	override val headers = HttpHeaders()
		get() = if (executed) { ReadOnlyHttpHeaders(field) } else { field }

	override val body: OutputStream
		get() {
			assert(!executed)
			return doGetBody(headers)
		}

	override fun execute(): HttpResponse {
		assert(!executed)
		return doExecute(headers).also { this.executed = true }
	}

	protected abstract fun doGetBody(headers: HttpHeaders): OutputStream

	protected abstract fun doExecute(headers: HttpHeaders): HttpResponse
}