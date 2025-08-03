import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.KrutHttpHandler
import utils.routeNotFoundResponse
import utils.toKrutRequest
import java.net.InetSocketAddress
import java.util.concurrent.Executors

class KrutApp {
    private val routes = mutableListOf<KrutRoute>()

    fun get(path: String, handler: suspend KrutRequest.() -> KrutResponse) {
        val route = buildRoute(
            method = KrutMethod.GET,
            path = path,
            handler = handler
        )

        routes.add(route)
    }

    fun post(path: String, handler: suspend KrutRequest.() -> KrutResponse) {
        val route = buildRoute(
            method = KrutMethod.POST,
            path = path,
            handler = handler
        )

        routes.add(route)
    }

    fun put(path: String, handler: suspend KrutRequest.() -> KrutResponse) {
        val route = buildRoute(
            method = KrutMethod.PUT,
            path = path,
            handler = handler
        )

        routes.add(route)
    }

    fun delete(path: String, handler: suspend KrutRequest.() -> KrutResponse) {
        val route = buildRoute(
            method = KrutMethod.DELETE,
            path = path,
            handler = handler
        )

        routes.add(route)
    }

    private fun buildRoute(
        method: KrutMethod,
        path: String,
        handler: suspend KrutRequest.() -> KrutResponse
    ): KrutRoute {
        val (pathRegex, paramNames) = compilePath(path)

        return KrutRoute(
            method = method,
            path = path,
            pathRegex = pathRegex,
            paramNames = paramNames,
            handler = handler
        )
    }

    fun listen(port: Int = 8080, host: String = "0.0.0.0") {
        val server = HttpServer.create(InetSocketAddress(host, port), 0)
        val dispatcher = Executors.newCachedThreadPool()
        val scope = CoroutineScope(Dispatchers.IO)
        val handler = KrutHttpHandler(getRoutes = { routes })

        server.createContext("/") { exchange ->
            scope.launch { handler.handle(exchange) }
        }

        server.executor = dispatcher
        server.start()

        println("Krut running at http://${if (host == "0.0.0.0") "localhost" else host}:$port")
    }
}