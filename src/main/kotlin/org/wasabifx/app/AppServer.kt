package org.wasabifx.app

import io.netty.handler.codec.http.HttpMethod
import org.slf4j.LoggerFactory
import org.wasabifx.deserializers.Deserializer
import org.wasabifx.deserializers.JsonDeserializer
import org.wasabifx.deserializers.MultiPartFormDataDeserializer
import org.wasabifx.exceptions.RouteAlreadyExistsException
import org.wasabifx.interceptors.*
import org.wasabifx.protocol.http.HttpServer
import org.wasabifx.protocol.http.StatusCodes
import org.wasabifx.routing.*
import org.wasabifx.serializers.JsonSerializer
import org.wasabifx.serializers.Serializer
import org.wasabifx.serializers.TextPlainSerializer
import org.wasabifx.serializers.XmlSerializer
import org.wasabifx.protocol.websocket.Channel
import org.wasabifx.protocol.websocket.ChannelHandler
import java.util.*
import kotlin.reflect.KClass


public open class AppServer(val configuration: AppConfiguration = AppConfiguration()) {

    private val logger = LoggerFactory.getLogger(AppServer::class.java)
    private val httpServer: HttpServer
    private var running = false

    public val routes: ArrayList<Route> = ArrayList<Route>()
    public val channels: ArrayList<Channel> = ArrayList<Channel>()
    public val exceptionHandlers: MutableSet<RouteException> = mutableSetOf()
    public val interceptors : ArrayList<InterceptorEntry>  = ArrayList<InterceptorEntry>()
    public val serializers: ArrayList<Serializer> = arrayListOf(JsonSerializer(), XmlSerializer(), TextPlainSerializer())
    public val deserializers: ArrayList<Deserializer> = arrayListOf(MultiPartFormDataDeserializer(), JsonDeserializer())

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

    public fun init() {
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
    public val isRunning: Boolean
        get ()
        {
            return running
        }

    /**
     * Starts the server
     *
     * @param   wait
     */
    public fun start(wait: Boolean = true) {
        logger!!.info(configuration.welcomeMessage)

        running = true
        httpServer.start(wait)

    }

    /**
    * Stops the server
    *
    */
    public fun stop() {
        httpServer.stop()
        running = false
        logger!!.info("Server Stopped")
    }


    /**
     *
     */
    public fun get(path: String, vararg handlers: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.GET, path, *handlers)
    }

    public fun post(path: String, vararg handlers: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.POST, path, *handlers)
    }

    public fun put(path: String, vararg handlers: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.PUT, path, *handlers)
    }

    public fun head(path: String, vararg handlers: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.HEAD, path, *handlers)
    }

    public fun delete(path: String, vararg handlers: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.DELETE, path, *handlers)
    }

    public fun options(path: String, vararg handler: RouteHandler.() -> Unit) {
        addRoute(HttpMethod.OPTIONS, path, *handler)
    }

    public fun patch(path: String, vararg handler: RouteHandler.() -> Unit) {
        // TODO: Check
        addRoute(HttpMethod.PATCH, path, *handler)
    }

    public fun channel(path: String, handler: ChannelHandler.() -> Unit) {
        addChannel(path, handler)
    }

    public fun exception(exception: KClass<*>, handler: ExceptionHandler.() -> Unit) {
        addExceptionHandler(exception.qualifiedName!!, handler)
    }

    public fun exception(handler: ExceptionHandler.() -> Unit) {
        addExceptionHandler("", handler)
    }

    public fun intercept(interceptor: Interceptor, path: String = "*", interceptOn: InterceptOn = InterceptOn.PreExecution) {
        interceptors.add(InterceptorEntry(interceptor, path, interceptOn))
    }
}


