package org.wasabi.http

public trait ParserLocator {
    fun locateParser(contentType: String): BodyParser?
}