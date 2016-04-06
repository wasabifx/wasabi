package org.wasabi.protocol.http

import java.util.ArrayList

public class CORSEntry(val path: String = "*",
                       val origins: String = "*",
                       val methods: String = "GET, POST, PUT, DELETE",
                       val headers: String = "Origin, X-Requested-With, Content-Type, Accept",
                       val credentials: String = "",
                       val preflightMaxAge: String = "") {

}