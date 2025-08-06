package utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

object KrutSerializer  {
    val json = Json {}

    inline fun <reified T: Any> serialize(data: T): String {
        return json.encodeToString(serializer =  serializer(), value = data)
    }

    inline fun <reified T: Any> deserialize(json: String): T {
        return this.json.decodeFromString(json)
    }
}