package network

import KrutResponse
import KrutRoute
import com.sun.net.httpserver.HttpExchange
import toRequest
import java.io.ByteArrayInputStream

class HttpExchangeHandler(
    private val getRoutes: () -> List<KrutRoute>
) {
    suspend fun handle(exchange: HttpExchange) {
        val routes = getRoutes()

        try {
            val path = exchange.requestURI.path.removeSuffix("/")
            val matchedRoute = routes.find {
                it.method.name == exchange.requestMethod && (it.path == path || it.pathRegex.matches(path))
            }

            val response = if (matchedRoute == null) {
                KrutResponse(
                    status = 404,
                    headers = mapOf(),
                    body = ByteArrayInputStream("Route not found".toByteArray())
                )
            } else {
                val pathValues = matchedRoute.pathRegex.matchEntire(path)?.groupValues?.drop(1).orEmpty()
                val pathParams = matchedRoute.pathNames.zip(pathValues).toMap()

                val request = exchange.toRequest(pathParams)
                matchedRoute.handler.invoke(request)
            }

            response.headers.forEach { (header, value) ->
                exchange.responseHeaders.add(header, value)
            }
            exchange.sendResponseHeaders(response.status, 0)
            exchange.responseBody.use { outStream ->
                response.body.use { stream ->
                    stream.copyTo(outStream)
                }
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            exchange.sendResponseHeaders(500, 0)
            exchange.responseBody.use { it.write("Internal Server Error".toByteArray()) }
        }
    }
}