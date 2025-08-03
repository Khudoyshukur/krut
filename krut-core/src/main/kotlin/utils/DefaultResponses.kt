package utils

import model.MessageResponse
import respondJson

internal fun routeNotFoundResponse(path: String) = respondJson(
    data = MessageResponse(message = "Route $path not found")
)