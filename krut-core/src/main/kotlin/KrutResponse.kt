import utils.KrutSerializer

class KrutResponse(
    val status: Int,
    val headers: Map<String, String>,
    val body: ByteArray
)

fun respondText(text: String, status: Int = 200): KrutResponse {
    return KrutResponse(
        status = status,
        headers = mapOf("Content-Type" to "text/plain"),
        body = text.toByteArray()
    )
}

inline fun <reified T: Any> respondJson(data: T, status: Int = 200): KrutResponse {
    return KrutResponse(
        status = status,
        headers = mapOf("Content-Type" to "application/json"),
        body = KrutSerializer.serialize(data).toByteArray()
    )
}

inline fun <reified T: Any> respondJson200(data: T): KrutResponse {
    return respondJson(data, status = 200)
}

inline fun <reified T: Any> respondJson404(data: T): KrutResponse {
    return respondJson(data, status = 404)
}