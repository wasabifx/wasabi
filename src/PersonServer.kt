package personserver

import org.wasabi.app.AppServer
import org.wasabi.interceptors.negotiateContent
import java.util.ArrayList
import java.util.HashMap
import org.wasabi.interceptors.serveFavIconAs
import java.util.Date
import java.util.Calendar
import org.wasabi.http.HttpStatusCodes
import org.wasabi.http.with


data class Person(val id: Int, val name: String, val email: String, val profession: String, val dateJoined: Date, val level: Int)






val people =  arrayListOf<Person>(
        Person(1, "Hadi Hariri", "mail@somewhere.com", "Developer", setDate(2005, 12, 10), 3),
        Person(2, "Joe Smith", "joe@somewhere.com", "Marketing", setDate(2007, 11, 3), 2),
        Person(3, "Jenny Jackson", "jenny@gmail.com", "Non Sleeper", setDate(2011, 6, 3), 1))



fun main(args: Array<String>) {



    val appServer = AppServer()

    appServer.negotiateContent()


    appServer.get("/person", getPersons)

    appServer.get("/person/:id", getPersonById)

    appServer.post("/person", createPerson

    )


    appServer.options("/person",
    {
        response.addExtraHeader("Allow", "GET, PUT, POST")
        response.setHttpStatus(HttpStatusCodes.OK)

    })


    appServer.start()
}




fun setDate(year: Int, month: Int, day: Int): Date {
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, day);
    return cal.getTime()
}