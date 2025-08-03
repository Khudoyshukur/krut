package utils

import KrutMethod
import KrutRequest
import com.sun.net.httpserver.HttpExchange

internal fun HttpExchange.toKrutRequest(pathParams: Map<String, String>): KrutRequest {
    return KrutRequest(
        method = KrutMethod.valueOf(this.requestMethod),
        path = this.requestURI.path,
        headers = this.getHeaders(),
        queryParams = this.getQueryParams(),
        pathParams = pathParams,
        bodyStream = this.requestBody
    )
}

internal fun HttpExchange.getQueryParams(): Map<String, String> {
    val queryParams = mutableMapOf<String, String>()
    this.requestURI.query?.split("&")?.map { it.trim().split("=") }
        ?.forEach { parts ->
            parts.getOrNull(0)?.let { query -> queryParams[query] = parts.getOrNull(1).orEmpty() }
        }

    return queryParams
}

internal fun HttpExchange.getHeaders(): Map<String, String> {
    val headers = mutableMapOf<String, String>()
    this.requestHeaders.forEach { header, values -> headers[header] = values.last() }

    return headers
}