
import org.wasabi
import javax.security.auth.login.Configuration
import org.wasabi.http.HttpServer
import org.wasabi.app.AppConfiguration
import org.wasabi.app.AppServer
import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.routing.RouteHandler
import org.wasabi.routing.*
import org.wasabi.http.ContentType
import org.wasabi.interceptors.BasicAuthenticationInterceptor
import org.wasabi.http.CacheControl
import org.wasabi.http.NegotiateOn
import org.wasabi.interceptors.ContentNegotiationPrioritizerInterceptor
import org.wasabi.interceptors.conneg
import Testing.*
import org.wasabi.interceptors.serveStaticFilesFromFolder
import org.wasabi.interceptors.serveFavIconAs
import org.wasabi.interceptors.serveErrorsFromFolder
import org.wasabi.interceptors.conneg
import org.wasabi.interceptors.negotiateContent


fun main(args: Array<String>) {

    val server = AppServer()

    // customer/10?format=json // customer/10.json // Accept: application/json


    // customer/10.json


    server.conneg() {
        onAcceptHeader()
        onExtension()
        onQueryParameter("format")
    }


    server.intercept(ContentNegotiationPrioritizerInterceptor().onAcceptHeader().onExtension().onQueryParameter("format"))


   //       server.intercept(BasicAuthenticationInterceptor("secure area", { (user: String, pass: String) -> user == pass}), "*")

    server.serveStaticFilesFromFolder("/public")
    server.serveFavIconAs("/public/favicon.ico")
    server.serveErrorsFromFolder("/public")

    server.negotiateContent()

        //    fun any(handler : RouteHandler.() -> Unit) : Pair<String, RouteHandler.() -> Unit> = "**/*//*" to handler
        //    fun String.to(handler : RouteHandler.() -> Unit) : Pair<String, RouteHandler.() -> Unit> = this to handler

        /*
            server.getx("/customer",
                    any {

                    },
                    "text/html" to {
                        // return a web page
                    },
                    "application/zip" to {
                        // return application/zip as it states...
                    }

                    )


        */


        server.get("/book",
                {

                    log.write("Logging it all out")
                    next()
                },

                {
                    negotiate {

                        on("text/html") {response.send("Something about me")}

                        on("application/json") {
                            response.send("{title: 'Something about me', isbn: 'IS454-12123-A23232'}")
                        }
                    }

                    response.send(Book("Something about me", "IS454-12123-A23232", "Biography", Author("Joe", "Smith")))
                }
        )




        server.get("/customer/:id",
                {
                    response.send(customer)
                })
        server.get("/all", someHandler)
       // server.get("/onmore", ::someOtherFunc)

        server.start()



}





public class Author(val firstName: String, val lastName: String) {

}
class Book(val title: String, val isbn: String, val genre: String, val author: Author) {

}
fun someOtherFunc() {

}

val basicAuthentication: RouteHandler.() -> Unit = {
    response.send("a")
    next()
}

object customer {

}
object log {
fun write(a: String) {

}
}



/*
this.contentType = contentType.toString()

        val objectMapper = ObjectMapper()


buffer = objectMapper.writeValueAsString(obj)!!
*/
