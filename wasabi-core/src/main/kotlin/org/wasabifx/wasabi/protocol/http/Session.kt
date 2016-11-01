package org.wasabifx.wasabi.protocol.http

import org.joda.time.DateTime


class Session(val id: String) {
    // TODO wire in use of config setting
    var expirationDate = DateTime.now()!!.plusSeconds(600)
    var data: Any? = null

    fun extendSession()
    {
        expirationDate = DateTime.now()!!.plusSeconds(600)
    }
}