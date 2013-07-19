package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.http.NegotiateOn
import org.wasabi.app.AppServer
import org.wasabi.routing.InterceptOn
import java.util.PriorityQueue


public class ConnegInterceptor(): Interceptor {
    val ACCEPT_HEADER = 0
    val QUERY_PARAM = 1
    val EXTENSION = 2
    val orderQueue = PriorityQueue<Int>()
    var queryParameterName = ""

    override fun intercept(request: Request, response: Response): Boolean {
        var contentType = ""

        while (contentType == "" && orderQueue.size() > 0) {
            var connegType = orderQueue.poll()
            when (connegType) {
                ACCEPT_HEADER -> contentType = request.accept.makeString(",")
                QUERY_PARAM -> contentType = request.queryParams[queryParameterName] ?: ""
                EXTENSION -> contentType = request.uri // fix to get actual .extension
                else -> {
                    throw IllegalArgumentException("unknown conneg")
                }
            }

        }
        if (contentType != "") {
            // find serializer and serialize.
        }
        return true
    }

    fun onAcceptHeader(): ConnegInterceptor {
        orderQueue.add(ACCEPT_HEADER)
        return this
    }

    fun onQueryParameter(queryParameterName: String = "format"): ConnegInterceptor {
        this.queryParameterName = queryParameterName
        orderQueue.add(QUERY_PARAM)
        return this
    }

    fun onExtension(): ConnegInterceptor {
        orderQueue.add(EXTENSION)
        return this
    }

}

fun AppServer.conneg(path: String = "*", body:ConnegInterceptor.()->Unit)  {
    val conneg = ConnegInterceptor()
    conneg.body()
    intercept(conneg, path, InterceptOn.PostRequest)
}