package network

import KrutMethod
import KrutRoute
import com.sun.net.httpserver.HttpExchange
import utils.routeNotFoundResponse
import utils.toKrutRequest

class KrutHttpHandler(
    private val getRoutes: () -> List<KrutRoute>
) {
    suspend fun handle(exchange: HttpExchange) {
        val routes = getRoutes()

        try {
            val requestPath = exchange.requestURI.path
            val matchedRoute = routes.find {
                it.method == KrutMethod.valueOfOrNull(exchange.requestMethod) &&
                        it.pathRegex.matches(requestPath)
            }

            val response = if (matchedRoute == null) {
                routeNotFoundResponse(path = requestPath)
            } else {
                val match = matchedRoute.pathRegex.matchEntire(requestPath)!!
                val paramValues = match.groupValues.drop(1)
                val pathParams = matchedRoute.paramNames.zip(paramValues).toMap()
                val request = exchange.toKrutRequest(pathParams)
                matchedRoute.handler.invoke(request)
            }

            response.headers.forEach { (key, value) ->
                exchange.responseHeaders.add(key, value)
            }
            exchange.sendResponseHeaders(response.status, response.body.size.toLong())
            exchange.responseBody.use { it.write(response.body) }

        } catch (e: Exception) {
            e.printStackTrace()
            exchange.sendResponseHeaders(500, 0)
            exchange.responseBody.use { it.write("Internal Server Error".toByteArray()) }
        }
    }
}