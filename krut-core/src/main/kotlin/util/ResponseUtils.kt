package util

import KrutResponse
import serialization.KrutJsonSerializer

inline fun <reified T: Any> respondJson(
    status: Int,
    headers: Map<String, String> = mapOf(),
    body: T
): KrutResponse {
    val jsonBody = KrutJsonSerializer.serialize(body)

    return KrutResponse(
        status = status,
        headers = headers
            .plus(
                "Content-Type" to "application/json"
            ),
        body = jsonBody.toByteArray()
    )
}