package org.wasabi.http

public trait BodyParser {
    fun parseBody(rawBody: String): BodyParams
}