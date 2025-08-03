typealias KrutMiddleWare = suspend (req: KrutRequest, next: KrutHandler) -> KrutResponse

fun chainMiddleWares(
    handler: KrutHandler,
    middleWares: List<KrutMiddleWare>
): KrutHandler {
    return middleWares.reversed().fold(handler) { next, middleware ->
        { req -> middleware(req, next) }
    }
}