package org.wasabifx.wasabi.routing

import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response

class ExceptionHandler(val request: Request, val response: Response, val exception: Exception)  {}

fun exceptionHandler(f: ExceptionHandler.()->Unit) = f