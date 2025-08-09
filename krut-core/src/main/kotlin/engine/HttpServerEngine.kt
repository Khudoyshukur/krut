package engine

import KrutRoute
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.*
import network.HttpExchangeHandler
import java.net.InetSocketAddress

class HttpServerEngine(
    private val host: String,
    private val port: Int,
    private val routes: () -> List<KrutRoute>
): KrutEngine {
    private val server = HttpServer.create(InetSocketAddress(host, port), 0)
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun start() {
        val handler = HttpExchangeHandler(getRoutes = { routes() })

        server.createContext("/") {
            coroutineScope.launch { handler.handle(it) }
        }

        server.start()
    }

    override fun stop() {
        server.stop(1)
        coroutineScope.cancel()
    }
}