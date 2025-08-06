package engine

import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.*
import network.KrutHttpHandler
import java.net.InetSocketAddress
import java.util.concurrent.Executors

class HttpServerEngine(
    port: Int = 8080,
    host: String = "0.0.0.0",
    private val handler: KrutHttpHandler
): KrutEngine {
    private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private val server = HttpServer.create(InetSocketAddress(host, port), 0)

    override fun start() {
        server.createContext("/") { exchange ->
            scope.launch { handler.handle(exchange) }
        }

        server.start()
    }

    override fun stop() {
        server.stop(1)
        scope.cancel()
        dispatcher.close()
    }
}