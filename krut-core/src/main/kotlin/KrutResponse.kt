import java.io.InputStream

class KrutResponse(
    val status: Int,
    val headers: Map<String, String>,
    val body: InputStream
)