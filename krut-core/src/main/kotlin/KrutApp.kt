import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import network.HttpExchangeHandler
import java.net.InetSocketAddress

class KrutApp {

    private val routes = mutableListOf<KrutRoute>()

    fun get(path: String, handler: KrutHandler) {
        val route = buildRoute(method = KrutMethod.GET, path, handler)
        routes.add(route)
    }

    fun post(path: String, handler: KrutHandler) {
        val route = buildRoute(method = KrutMethod.POST, path, handler)
        routes.add(route)
    }

    fun put(path: String, handler: KrutHandler) {
        val route = buildRoute(method = KrutMethod.PUT, path, handler)
        routes.add(route)
    }

    fun delete(path: String, handler: KrutHandler) {
        val route = buildRoute(method = KrutMethod.DELETE, path, handler)
        routes.add(route)
    }

    private fun buildRoute(
        method: KrutMethod,
        path: String,
        handler: KrutHandler
    ): KrutRoute {
        val formattedPath = path.removeSuffix("/")
        val (regex, pathNames) = getRegexAndPathNames(formattedPath)

        return KrutRoute(
            method = method,
            path = formattedPath,
            handler = handler,
            pathRegex = regex,
            pathNames = pathNames
        )
    }


    fun listen(
        port: Int,
        host: String
    ) {
        val server = HttpServer.create(InetSocketAddress(host, port), 0)
        val handler = HttpExchangeHandler(getRoutes = { routes })
        val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        server.createContext("/") {
            coroutineScope.launch { handler.handle(it) }
        }

        server.start()
    }
}