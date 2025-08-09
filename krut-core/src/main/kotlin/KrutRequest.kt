import com.sun.net.httpserver.HttpExchange
import jakarta.servlet.http.HttpServletRequest
import serialization.KrutJsonSerializer
import java.io.InputStream
import java.nio.charset.Charset

data class KrutRequest(
    val method: KrutMethod,
    val path: String,
    private val headers: Map<String, String>,
    val queryParams: Map<String, String>,
    val pathParams: Map<String, String>,
    val body: InputStream
) {
    fun getHeader(header: String): String? {
        return headers[header] ?: headers[header.lowercase()]
    }
}

inline fun <reified T: Any> KrutRequest.body(): T {
    val json = this.body.readAllBytes().toString(Charset.defaultCharset())

    return KrutJsonSerializer.deserialize(json)
}

internal fun HttpServletRequest.toKrutRequest(pathParams: Map<String, String>): KrutRequest {
    val headers = headerNames.toList().associateWith { getHeader(it) }
    val queryParams = parameterMap.mapValues { it.value.firstOrNull() ?: "" }

    return KrutRequest(
        method = KrutMethod.valueOf(method),
        path = requestURI,
        queryParams = queryParams,
        headers = headers,
        body = inputStream,
        pathParams = pathParams
    )
}

fun HttpExchange.toRequest(
    pathParams: Map<String, String>
): KrutRequest {
    return KrutRequest(
        method = KrutMethod.valueOf(this.requestMethod),
        path = this.requestURI.path,
        headers = this.getHeaders() ,
        queryParams = getQueryParams(),
        pathParams = pathParams,
        body = this.requestBody
    )
}

fun HttpExchange.getHeaders(): MutableMap<String, String> {
    val headers = mutableMapOf<String, String>()

    this.requestHeaders.keys.forEach { header ->
        val value = this.requestHeaders.getFirst(header)
        headers[header] = value
    }

    return headers
}

fun HttpExchange.getQueryParams(): Map<String, String> {
    val params = mutableMapOf<String, String>()
    if (this.requestURI.query.isNullOrBlank()) return params

    val queries = this.requestURI.query.split("&")
    queries.forEach { query ->
        val parts = query.split("=")
        params[parts[0]] = parts[1]
    }

    return params
}