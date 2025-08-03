package engine

import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.KrutHttpHandler
import java.net.InetSocketAddress
import java.util.concurrent.Executors

class HttpServerEngine(
    port: Int = 8080,
    host: String = "0.0.0.0",
    private val handler: KrutHttpHandler
): KrutEngine {
    private val server = HttpServer.create(InetSocketAddress(host, port), 0)

    override fun start() {
        val dispatcher = Executors.newCachedThreadPool()
        val scope = CoroutineScope(Dispatchers.IO)

        server.createContext("/") { exchange ->
            scope.launch { handler.handle(exchange) }
        }

        server.executor = dispatcher
        server.start()
    }

    override fun stop() {
        server.stop(0)
    }
}