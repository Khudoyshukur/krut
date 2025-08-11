package util

import KrutResponse
import serialization.KrutJsonSerializer
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files

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
        body = ByteArrayInputStream(jsonBody.toByteArray())
    )
}

fun respondFile(
    status: Int,
    headers: Map<String, String> = mapOf(),
    file: File
): KrutResponse {
    return KrutResponse(
        status = status,
        headers = headers
            .plus(
                "Content-Type" to (Files.probeContentType(file.toPath()) ?: "application/octet-stream")
            ),
        body = FileInputStream(file)
    )
}