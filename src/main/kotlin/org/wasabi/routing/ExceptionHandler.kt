package org.wasabi.routing

import org.wasabi.protocol.http.Request
import org.wasabi.protocol.http.Response

public class ExceptionHandler(public val request: Request, public val response: Response, public val exception: Exception)  {}

public fun exceptionHandler(f: ExceptionHandler.()->Unit) = f