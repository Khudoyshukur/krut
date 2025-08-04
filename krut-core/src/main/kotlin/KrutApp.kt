import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

class KrutApp {
    fun listen(
        port: Int,
        host: String
    ) {
        val server = HttpServer.create(InetSocketAddress(host, port), 0)

        server.createContext("/") {
            it.sendResponseHeaders(200, 0)
            it.responseBody.use { it.write("Hello World from Krut".toByteArray()) }
        }

        server.start()
    }
}