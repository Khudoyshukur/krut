import utils.KrutSerializer
import java.io.InputStream

class KrutRequest internal constructor(
    val method: KrutMethod,
    val path: String,
    val headers: Map<String, String>,
    val queryParams: Map<String, String>,
    val pathParams: Map<String, String>,
    private val bodyStream: InputStream
) {
    fun bodyAsText(): String {
        return bodyStream.bufferedReader().use { it.readText() }
    }
}

inline fun <reified T: Any> KrutRequest.body(): T {
    return KrutSerializer.deserialize(bodyAsText())
}

