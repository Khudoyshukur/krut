package serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

object KrutJsonSerializer {
    inline fun <reified T: Any> serialize(body: T): String {
        return Json.encodeToString(serializer(), body)
    }

    inline fun <reified T: Any> deserialize(body: String): T {
        return Json.decodeFromString(body)
    }
}