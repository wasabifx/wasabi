import com.hadihariri.wasabi
import javax.security.auth.login.Configuration
import com.hadihariri.wasabi.http.HttpServer
import com.hadihariri.wasabi.app.AppConfiguration
import com.hadihariri.wasabi.app.AppServer

fun main(args: Array<String>) {

    val server = AppServer()

   // server.get("/", { req, res -> res.send("object")})

    server.routes.get("/",{ req, res -> res.send("Hello")})
    server.start()
}