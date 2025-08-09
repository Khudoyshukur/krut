package network

import KrutResponse
import KrutRoute
import com.sun.net.httpserver.HttpExchange
import toRequest

class HttpExchangeHandler(
    private val getRoutes: () -> List<KrutRoute>
) {
    suspend fun handle(exchange: HttpExchange) {
        val routes = getRoutes()

        try {
            val path = exchange.requestURI.path
            val matchedRoute = routes.find { it.path == path }

            val response = if (matchedRoute == null) {
                KrutResponse(
                    status = 404,
                    headers = mapOf(),
                    body = "Route not found".toByteArray()
                )
            } else {
                val request = exchange.toRequest()
                matchedRoute.handler.invoke(request)
            }

            response.headers.forEach { (header, value) ->
                exchange.responseHeaders.add(header, value)
            }
            exchange.sendResponseHeaders(response.status, 0)
            exchange.responseBody.use { it.write(response.body) }
        } catch (throwable: Throwable) {
            exchange.sendResponseHeaders(500, 0)
            exchange.responseBody.use { it.write("Internal Server Error".toByteArray()) }
        }
    }
}