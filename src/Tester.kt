import org.wasabi
import javax.security.auth.login.Configuration
import org.wasabi.http.HttpServer
import org.wasabi.app.AppConfiguration
import org.wasabi.app.AppServer
import org.wasabi.routing.Routes
import org.wasabi.http.Request
import org.wasabi.http.Response

fun main(args: Array<String>) {

    val server = AppServer()

   // server.get("/", { req, res -> res.send("object")})

    Routes.get("/good",{ req, res -> res.send("Well this means that routes now work!")})
    Routes.get("/",{ req, res -> res.send("Hello, how are you")})
    server.start()

    verb("/customer", {

    })
}


fun verb(url: String, handler: VerbHandler.() -> Unit) {

}

public class VerbHandler(val request: Request, val response: Response) {

    public fun send(obj: Any) {

    }

}