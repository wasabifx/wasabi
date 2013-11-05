package org.wasabi

import org.wasabi.app.AppServer
import java.util.ArrayList
import java.util.HashMap
import org.wasabi.interceptors.serveFavIconAs
import java.util.Date
import java.util.Calendar
import org.wasabi.http.StatusCodes
import org.wasabi.getPersons
import org.wasabi.getPersonById
import org.wasabi.createPerson
import org.wasabi.http.ContentType
import org.wasabi.app.AppConfiguration
import org.wasabi.configuration.ConfigurationStorage
import org.wasabi.interceptors.enableAutoOptions
import org.wasabi.interceptors.enableCORS
import org.wasabi.http.CORSEntry
import org.wasabi.interceptors.enableCORSGlobally
import org.wasabi.interceptors.enableContentNegotiation
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import org.slf4j.LoggerFactory


data class Person(val id: Int, val name: String, val email: String, val profession: String, val dateJoined: Date, val level: Int)






val people =  arrayListOf<Person>(
        Person(1, "Hadi Hariri", "mail@somewhere.com", "Developer", setDate(2005, 12, 10), 3),
        Person(2, "Joe Smith", "joe@somewhere.com", "Marketing", setDate(2007, 11, 3), 2),
        Person(3, "Jenny Jackson", "jenny@gmail.com", "Non Sleeper", setDate(2011, 6, 3), 1))

private var log = LoggerFactory.getLogger(javaClass<AppServer>())

fun main(args: Array<String>) {



    val appServer = AppServer()

    appServer.enableContentNegotiation()
    appServer.enableAutoOptions()
    appServer.enableCORSGlobally()


    appServer.get("/person", getPersons)

    appServer.get("/person/:id", getPersonById)

    appServer.post("/person", createPerson)

    appServer.get("/redirect", {

      response.redirect("http://www.google.com")

    })


    appServer.get("/js", { response.send(people, "application/json") })

    appServer.channel("/person", {
        if (frame is PingWebSocketFrame)
        {
            ctx?.channel()?.write(PongWebSocketFrame())
        }


        if (!(frame is TextWebSocketFrame)) {
            throw UnsupportedOperationException();
        }

        /**if ( webSocketFrame is BinaryWebSocketFrame) {

        }*/

        // Send the uppercase string back.
        var frame = frame as TextWebSocketFrame
        var foo = frame.text()

        log!!.info("Received ${foo}")

        ctx?.channel()?.write(TextWebSocketFrame(foo?.toUpperCase()))
    })


    appServer.start()
}




fun setDate(year: Int, month: Int, day: Int): Date {
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, day);
    return cal.getTime()
}