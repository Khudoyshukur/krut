import com.sun.net.httpserver.HttpExchange
import java.io.InputStream

data class KrutRequest(
    val method: KrutMethod,
    val path: String,
    val headers: Map<String, String>,
    val queryParams: Map<String, String>,
    val pathParams: Map<String, String>,
    val body: InputStream
)

fun HttpExchange.toRequest(): KrutRequest {
    return KrutRequest(
        method = KrutMethod.valueOf(this.requestMethod),
        path = this.requestURI.path,
        headers = this.getHeaders() ,
        queryParams = mapOf(),
        pathParams = mapOf(),
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