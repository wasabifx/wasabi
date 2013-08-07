
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
import org.wasabi.interceptors.ConnegInterceptor
import org.wasabi.interceptors.conneg
import Testing.*
import org.wasabi.interceptors.static
import org.wasabi.interceptors.favicon


fun main(args: Array<String>) {

    val server = AppServer()

    // customer/10?format=json // customer/10.json // Accept: application/json


    // customer/10.json


    server.conneg() {
        onAcceptHeader()
        onExtension()
        onQueryParameter("format")
    }


    server.intercept(ConnegInterceptor().onAcceptHeader().onExtension().onQueryParameter("format"))


          server.intercept(BasicAuthenticationInterceptor("secure area", { (user: String, pass: String) -> user == pass}), "*")

    server.static("/public")
    server.favicon("/public/favicon.ico")



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
                    response.send(customer)
                })

        server.get("/all", someHandler)
       // server.get("/onmore", ::someOtherFunc)

        server.start()



}


fun someOtherFunc() {

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

