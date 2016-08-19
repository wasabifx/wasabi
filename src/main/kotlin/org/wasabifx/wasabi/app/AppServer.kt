package org.wasabifx.wasabi.app

import io.netty.handler.codec.http.HttpMethod
import org.slf4j.LoggerFactory
import org.wasabifx.wasabi.deserializers.Deserializer
import org.wasabifx.wasabi.deserializers.JsonDeserializer
import org.wasabifx.wasabi.deserializers.MultiPartFormDataDeserializer
import org.wasabifx.wasabi.protocol.http.HttpServer
import org.wasabifx.wasabi.protocol.http.StatusCodes
import org.wasabifx.wasabi.routing.*
import org.wasabifx.wasabi.interceptors.*
import org.wasabifx.wasabi.serializers.JsonSerializer
import org.wasabifx.wasabi.serializers.Serializer
import org.wasabifx.wasabi.serializers.TextPlainSerializer
import org.wasabifx.wasabi.serializers.XmlSerializer
import org.wasabifx.wasabi.protocol.websocket.Channel
import org.wasabifx.wasabi.protocol.websocket.ChannelHandler
import java.util.*
import kotlin.reflect.KClass


open class AppServer(val configuration: AppConfiguration = AppConfiguration()) {

    private val logger = LoggerFactory.getLogger(AppServer::class.java)
    private val httpServer: HttpServer
    private var running = false

    val routes: ArrayList<Route> = ArrayList<Route>()
    val channels: ArrayList<Channel> = ArrayList<Channel>()
    val exceptionHandlers: MutableSet<RouteException> = mutableSetOf()
    val interceptors : ArrayList<InterceptorEntry>  = ArrayList<InterceptorEntry>()
    val serializers: ArrayList<Serializer> = arrayListOf(JsonSerializer(), XmlSerializer(), TextPlainSerializer())
    val deserializers: ArrayList<Deserializer> = arrayListOf(MultiPartFormDataDeserializer(), JsonDeserializer())

    init {
        httpServer = HttpServer(this)
        init()
    }

    private fun addRoute(method: HttpMethod, path: String, vararg handler: RouteHandler.() -> Unit) {
        val existingRoute = routes.filter { it.path == path && it.method == method }
        if (existingRoute.count() >= 1) {
            throw RouteAlreadyExistsException(existingRoute.firstOrNull()!!)
        }
        routes.add(Route(path, method, HashMap<String, String>(), *handler))
    }

    private fun addChannel(path: String, handler: ChannelHandler.() -> Unit) {
        val existingChannel = channels.filter{ it.path == path }
        if (existingChannel.count() >= 1) {
            throw ChannelAlreadyExistsException(existingChannel.firstOrNull()!!)
        }
        channels.add(Channel(path, handler))
    }

    private fun addExceptionHandler(exceptionClass: String, handler: ExceptionHandler.() -> Unit) {
        val newRouteException = RouteException(exceptionClass, handler)
        exceptionHandlers.remove(newRouteException)
        exceptionHandlers.add(newRouteException)
    }

    fun init() {
        if (configuration.enableLogging) {
            intercept(LoggingInterceptor())
        }
        if (configuration.enableContentNegotiation) {
            enableContentNegotiation()
        }
        if (configuration.enableAutoOptions) {
            enableAutoOptions()
        }
        if (configuration.enableCORSGlobally) {
            enableCORSGlobally()
        }
        exception {
            logger.error("Uncaught exception: ", exception)
            response.setStatus(StatusCodes.InternalServerError)
        }
    }

    /**
     *  Returns true if the Server is running.
     */
    val isRunning: Boolean
        get ()
        {
            return running
        }

    /**
     * Starts the server
     *
     * @param   wait
     */
    fun start(wait: Boolean = true) {
        logger!!.info(configuration.welcomeMessage)

        running = true
        httpServer.start(wait)

    }

    /**
    * Stops the server
    *
    */
    fun stop() {
        httpServer.stop()
        running = false
        logger!!.info("Server Stopped")
    }


    /**
     *
     */
    fun get(path: String, vararg handlers: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.GET, path, *handlers)
    }

    fun post(path: String, vararg handlers: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.POST, path, *handlers)
    }

    fun put(path: String, vararg handlers: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.PUT, path, *handlers)
    }

    fun head(path: String, vararg handlers: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.HEAD, path, *handlers)
    }

    fun delete(path: String, vararg handlers: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.DELETE, path, *handlers)
    }

    fun options(path: String, vararg handler: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.OPTIONS, path, *handler)
    }

    fun patch(path: String, vararg handler: RouteHandler.() -> Unit) {
        // TODO: Check
        addRoute(HttpMethod.PATCH, path, *handler)
    }

    fun channel(path: String, handler: ChannelHandler.() -> Unit) {
        addChannel(path, handler)
    }

    fun exception(exception: KClass<*>, handler: ExceptionHandler.() -> Unit) {
        addExceptionHandler(exception.qualifiedName!!, handler)
    }

    fun exception(handler: ExceptionHandler.() -> Unit) {
        addExceptionHandler("", handler)
    }

    fun intercept(interceptor: Interceptor, path: String = "*", interceptOn: InterceptOn = InterceptOn.PreExecution) {
        interceptors.add(InterceptorEntry(interceptor, path, interceptOn))
    }
}


