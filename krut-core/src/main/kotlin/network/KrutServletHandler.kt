package network

import KrutMethod
import KrutResponse
import KrutRoute
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import toKrutRequest
import java.io.ByteArrayInputStream

class KrutServletHandler(
    private val getRoutes: () -> List<KrutRoute>
) {
    suspend fun handle(req: HttpServletRequest, resp: HttpServletResponse) {
        val routes = getRoutes()

        try {
            val requestPath = req.requestURI
            val requestMethod = req.method

            val matchedRoute = routes.find {
                it.method == KrutMethod.valueOf(requestMethod) && it.pathRegex.matches(requestPath)
            }

            val response = if (matchedRoute == null) {
                KrutResponse(
                    status = 404,
                    headers = mapOf(),
                    body = ByteArrayInputStream("Route not found".toByteArray())
                )
            } else {
                val match = matchedRoute.pathRegex.matchEntire(requestPath)!!
                val paramValues = match.groupValues.drop(1)
                val pathParams = matchedRoute.pathNames.zip(paramValues).toMap()
                val request = req.toKrutRequest(pathParams)
                matchedRoute.handler.invoke(request)
            }

            resp.status = response.status
            response.headers.forEach { (key, value) ->
                resp.setHeader(key, value)
            }
            resp.outputStream.use { response.body.copyTo(it) }

        } catch (e: Exception) {
            e.printStackTrace()
            resp.status = 500
            resp.outputStream.use { it.write("Internal Server Error".toByteArray()) }
        }
    }
}