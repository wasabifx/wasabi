package org.wasabifx.wasabi.test

import org.wasabifx.wasabi.app.AppConfiguration
import java.util.Date
import org.wasabifx.wasabi.app.AppServer
import org.wasabifx.wasabi.interceptors.*
import org.wasabifx.wasabi.protocol.http.CORSEntry
import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response
import org.wasabifx.wasabi.protocol.http.StatusCodes
import org.wasabifx.wasabi.routing.routeHandler
import org.wasabifx.wasabi.routing.with
import java.util.Calendar

data class Person(val id: Int, val name: String, val email: String, val profession: String, val dateJoined: Date, val level: Int)






val people =  arrayListOf<Person>(
        Person(1, "Hadi Hariri", "mail@somewhere.com", "Developer", setDate(2005, 12, 10), 3),
        Person(2, "Joe Smith", "joe@somewhere.com", "Marketing", setDate(2007, 11, 3), 2),
        Person(3, "Jenny Jackson", "jenny@gmail.com", "Non Sleeper", setDate(2011, 6, 3), 1))



fun main(args: Array<String>) {




    val appServer = AppServer(AppConfiguration(port = 8080, enableLogging = false)).apply {
        intercept(object : Interceptor {
            override fun intercept(request: Request, response: Response): Boolean = true
        }, "/api/:param/things", InterceptOn.PreExecution)
        get("/api/:param1/things", {
            val paramValue = request.routeParams["param1"]
            if (paramValue == "abc123") {
                response.send("ok!")
            } else {
                println("request param got messed up. should be 'abc123' but was $paramValue")
                response.statusCode = 500
                response.send("sad")
            }
        })

    }

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

    val person = people.firstOrNull { it.id == request.routeParams["id"]?.toInt() }
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