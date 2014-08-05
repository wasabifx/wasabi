package org.wasabi.test

import org.wasabi.app.AppServer
import org.wasabi.interceptors.enableContentNegotiation
import org.wasabi.interceptors.parseContentNegotiationHeaders
import org.wasabi.http.StatusCodes
import org.wasabi.interceptors.enableAutoLocation
import org.wasabi.interceptors.enableETag
import org.wasabi.interceptors.serveStaticFilesFromFolder
import org.wasabi.routing.routeHandler
import java.util.Date
import org.wasabi.interceptors.enableAutoOptions
import org.wasabi.interceptors.enableCORS
import org.wasabi.http.CORSEntry
import org.wasabi.routing.with
import java.util.Calendar

data class Person(val id: Int, val name: String, val email: String, val profession: String, val dateJoined: Date, val level: Int)






val people =  arrayListOf<Person>(
        Person(1, "Hadi Hariri", "mail@somewhere.com", "Developer", setDate(2005, 12, 10), 3),
        Person(2, "Joe Smith", "joe@somewhere.com", "Marketing", setDate(2007, 11, 3), 2),
        Person(3, "Jenny Jackson", "jenny@gmail.com", "Non Sleeper", setDate(2011, 6, 3), 1))



fun main(args: Array<String>) {



    val appServer = AppServer()

    appServer.enableContentNegotiation()
    appServer.enableAutoOptions()
    appServer.enableCORS(arrayListOf(CORSEntry()))


    appServer.get("/person", getPersons)

    appServer.get("/person/:id", getPersonById)

    appServer.post("/person", createPerson)

    appServer.get("/something", {

        response.negotiate (

                "text/html" with { send("Hello")},

                "application/json" with { send("..some custom Json?")}

        )


    })


    appServer.get("/js", { response.send(people, "application/json") })


    appServer.start()
}




fun setDate(year: Int, month: Int, day: Int): Date {
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, day);
    return cal.getTime()
}
data class Customer(val id: Int, val name: String)

val getPersons = routeHandler {

    response.send(people)
}

val getPersonById = routeHandler {

    val person = people.find { it.id == request.routeParams["id"]?.toInt()}
    if (person != null) {
        response.send(person)
    } else {
        response.setStatus(StatusCodes.NotFound)
    }
}

val createPerson = routeHandler {

    val person = Person(people.count()+1,
            request.bodyParams["name"].toString(),
            request.bodyParams["email"].toString(),
            request.bodyParams["profession"].toString(),
            Date(),
            1)
    people.add(person)
    response.resourceId = person.id.toString()

}