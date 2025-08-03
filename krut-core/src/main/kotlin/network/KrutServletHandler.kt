package network

import KrutRoute
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.runBlocking
import utils.routeNotFoundResponse
import utils.toKrutRequest

class KrutServletHandler(
    private val getRoutes: () -> List<KrutRoute>
) {
    fun handle(req: HttpServletRequest, resp: HttpServletResponse) = runBlocking {
        val routes = getRoutes()

        try {
            val requestPath = req.requestURI
            val requestMethod = req.method

            val matchedRoute = routes.find {
                it.method == KrutMethod.valueOfOrNull(requestMethod) &&
                        it.pathRegex.matches(requestPath)
            }

            val response = if (matchedRoute == null) {
                routeNotFoundResponse(requestPath)
            } else {
                val match = matchedRoute.pathRegex.matchEntire(requestPath)!!
                val paramValues = match.groupValues.drop(1)
                val pathParams = matchedRoute.paramNames.zip(paramValues).toMap()
                val request = req.toKrutRequest(pathParams)
                matchedRoute.handler.invoke(request)
            }

            resp.status = response.status
            response.headers.forEach { (key, value) ->
                resp.setHeader(key, value)
            }
            resp.outputStream.use { it.write(response.body) }

        } catch (e: Exception) {
            e.printStackTrace()
            resp.status = 500
            resp.outputStream.use { it.write("Internal Server Error".toByteArray()) }
        }
    }
}