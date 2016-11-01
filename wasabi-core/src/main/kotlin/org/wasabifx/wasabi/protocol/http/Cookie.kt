package org.wasabifx.wasabi.protocol.http

import io.netty.handler.codec.http.cookie.DefaultCookie


class Cookie(name: String?, value: String?) : DefaultCookie(name, value)