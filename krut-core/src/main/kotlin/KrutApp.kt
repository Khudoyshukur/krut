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
        val route = KrutRoute(
            method = KrutMethod.GET,
            path = path,
            handler = handler
        )
        routes.add(route)
    }

    fun post(path: String, handler: KrutHandler) {
        val route = KrutRoute(
            method = KrutMethod.POST,
            path = path,
            handler = handler
        )
        routes.add(route)
    }

    fun put(path: String, handler: KrutHandler) {
        val route = KrutRoute(
            method = KrutMethod.PUT,
            path = path,
            handler = handler
        )
        routes.add(route)
    }

    fun delete(path: String, handler: KrutHandler) {
        val route = KrutRoute(
            method = KrutMethod.DELETE,
            path = path,
            handler = handler
        )
        routes.add(route)
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