package org.wasabi.http

public enum class HttpMethod {
    GET
    PUT
    POST
    DELETE
    OPTIONS
    HEAD

}

// TODO: this shouldn't be here. Will move. It's temp
fun mapNettyHttpMethodToOwn(nettyMethod: io.netty.handler.codec.http.HttpMethod): HttpMethod {

    when (nettyMethod) {
        io.netty.handler.codec.http.HttpMethod.GET -> return HttpMethod.GET
        io.netty.handler.codec.http.HttpMethod.PUT -> return HttpMethod.PUT
        io.netty.handler.codec.http.HttpMethod.POST -> return HttpMethod.POST
        io.netty.handler.codec.http.HttpMethod.DELETE -> return HttpMethod.DELETE
        io.netty.handler.codec.http.HttpMethod.OPTIONS -> return HttpMethod.OPTIONS
        io.netty.handler.codec.http.HttpMethod.HEAD -> return HttpMethod.HEAD
        else -> {
            throw HttpMethodNotSupportedException(nettyMethod.toString())
        }
    }

}
