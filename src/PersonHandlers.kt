import org.wasabi.routing.RouteHandler
import java.util.ArrayList
import personserver.people
import org.wasabi.http.Response
import org.wasabi.http.Request
import org.wasabi.http.HttpStatusCodes
import org.wasabi.http.with
import org.wasabi.routing.routeHandler
import personserver.Person
import java.util.Date


val getPersons = routeHandler {
    response.send(people)
}




val getPersonById = routeHandler {
    val person = people.find { it.id == request.routeParams["id"]?.toInt()}
    if (person != null) {
        response.send(person)
    } else {
        response.setHttpStatus(HttpStatusCodes.NotFound)
    }
}
  // {"name":"Paul Stack", "email": "paul@stackit.com", "profession": "Vagrant Pusher"}
val createPerson = routeHandler {
    val person = Person(people.count()+1,
                        request.bodyParams["name"].toString(),
                        request.bodyParams["email"].toString(),
                        request.bodyParams["profession"].toString(),
                        Date(),
                        1)
    people.add(person)
    response.setHttpStatus(HttpStatusCodes.Created)
    response.location = "/person/${person.id}"
}



public class CustomerRoutes {

    class object {

        val createPerson = routeHandler {

        }

    }

}



