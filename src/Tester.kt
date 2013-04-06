import org.wasabi
import javax.security.auth.login.Configuration
import org.wasabi.http.HttpServer
import org.wasabi.app.AppConfiguration
import org.wasabi.app.AppServer
import org.wasabi.routing.Routes
import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.routing.RouteHandler
import org.wasabi.routing.*

fun main(args: Array<String>) {

    val server = AppServer()

   // server.get("/", { req, res -> res.send("object")})

    Routes.get("/good",{    response.send("Well this means that routes now work!")})
    Routes.get("/",{ response.send("Hello, how are you")})


    "/customer" post { response.send("abc") }



    server.start()

}







