package org.wasabifx.wasabi.protocol.http

import org.joda.time.DateTime


class Session(val id: String) {
    val TTL = 600

    // TODO wire in use of config setting
    var expirationDate = DateTime.now()!!.plusSeconds(TTL)
    var data: Any? = null

    fun extendSession()
    {
        expirationDate = DateTime.now()!!.plusSeconds(TTL)
    }
}