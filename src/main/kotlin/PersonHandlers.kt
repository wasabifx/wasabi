package org.wasabi

import org.wasabi.routing.RouteHandler
import java.util.ArrayList
import org.wasabi.http.Response
import org.wasabi.http.Request
import org.wasabi.http.StatusCodes
import org.wasabi.http.with
import org.wasabi.routing.routeHandler
import java.util.Date


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
    response.setStatus(StatusCodes.Created)
    response.location = "/person/${person.id}"

}
