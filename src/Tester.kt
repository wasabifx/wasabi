
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
import org.wasabi.interceptors.parseContentNegotiationHeaders
import org.wasabi.interceptors.serveStaticFilesFromFolder
import org.wasabi.interceptors.serveFavIconAs
import org.wasabi.interceptors.serveErrorsFromFolder
import org.wasabi.interceptors.parseContentNegotiationHeaders
import org.wasabi.interceptors.negotiateContent
import org.wasabi.http.Cookie
import org.wasabi.http.with
import org.wasabi.interceptors.ContentNegotiationParserInterceptor


fun main(args: Array<String>) {

    val server = AppServer()

    // customer/10?format=json // customer/10.json // Accept: application/json


    // customer/10.json


    server.parseContentNegotiationHeaders() {
        onAcceptHeader()
        onExtension()
        onQueryParameter("format")
    }


    server.intercept(ContentNegotiationParserInterceptor().onAcceptHeader().onExtension().onQueryParameter("format"))


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
                    response.negotiate (
                            "text/html" with { send("Something") }

                    )
                    //response.send(Book("Something about me", "IS454-12123-A23232", "Biography", Author("Joe", "Smith")))
                }
        )

        server.put("/customer", {
            response.send(request.bodyParams["name"].toString())
        })



        server.get("/set_cookie", {
            response.cookies["user"] = Cookie("user", "hadi", "/", "", false)
            response.send("cookie is set")
        })

        server.get("/get_cookie", {
            response.send(request.cookies["user"]!!.value)
        })
        server.get("/customer/:id",
                {
                    response.send(customer)
                })
      //  server.get("/onmore", ::someOtherFunc)

        server.start()



}





public class Author(val firstName: String, val lastName: String) {

}
class Book(val title: String, val isbn: String, val genre: String, val author: Author) {

}
fun RouteHandler.someOtherFunc() {

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
