
class KrutResponse(
    val status: Int,
    val headers: Map<String, String>,
    val body: ByteArray
)