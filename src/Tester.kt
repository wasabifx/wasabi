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

fun main(args: Array<String>) {

    val server = AppServer()

  //  server.intercept(BasicAuthenticationInterceptor("secure area", { (user: String, pass: String) -> user == pass}), "*")





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

    server.get("/customer",
         //   basicAuthentication,
            {
                log.write("Logging it all out")
                next()
            },

            {

                response.setCacheControl(CacheControl.NoCache)
                response.send("Something")
            }
    )





    server.get("/customer/:id",
            {

            })


    server.start()

}

val basicAuthentication: RouteHandler.() -> Unit = {
    response.send("a")
    next()
}

object customer {}
object log {
fun write(a: String) {

}
}

