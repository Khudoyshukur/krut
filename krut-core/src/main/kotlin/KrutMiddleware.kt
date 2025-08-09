
typealias KrutMiddleware = suspend (KrutRequest, KrutHandler) -> KrutResponse

fun chainMiddlewares(
    handler: KrutHandler,
    middlewares: List<KrutMiddleware>
): KrutHandler {
    // MW2--MW1--[Handler]--MW1--MW2

    var currentHandler = handler

    for (middleware in middlewares.reversed()) {
        val next = currentHandler
        currentHandler = { krutRequest -> middleware(krutRequest, next) }
    }

    return currentHandler
}