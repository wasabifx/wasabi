package org.wasabifx.routing

import org.wasabifx.protocol.http.Request
import org.wasabifx.protocol.http.Response

public class ExceptionHandler(public val request: Request, public val response: Response, public val exception: Exception)  {}

public fun exceptionHandler(f: ExceptionHandler.()->Unit) = f