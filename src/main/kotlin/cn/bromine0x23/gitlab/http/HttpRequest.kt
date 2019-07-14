/*
 * Copyright © 2019 Bromine0x23 <bromine0x23@163.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 */
package cn.bromine0x23.gitlab.http

import java.io.OutputStream
import java.net.URI

interface HttpRequest {

	val method: HttpMethod
		get() = HttpMethod.valueOf(methodValue)

	val methodValue: String

	val uri: URI

	val headers: HttpHeaders

	val body: OutputStream

	fun execute(): HttpResponse
}