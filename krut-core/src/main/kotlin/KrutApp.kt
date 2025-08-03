import engine.EngineType
import engine.HttpServerEngine
import engine.TomcatEngine
import network.KrutHttpHandler
import network.KrutServletHandler

class KrutApp(
    private val globalMiddleWares: List<KrutMiddleWare> = listOf()
) {
    private val routes = mutableListOf<KrutRoute>()

    fun get(
        path: String,
        middleWares: List<KrutMiddleWare> = listOf(),
        handler: KrutHandler,
    ) {
        val route = buildRoute(
            method = KrutMethod.GET,
            path = path,
            handler = handler,
            middleWares = middleWares
        )

        routes.add(route)
    }

    fun post(
        path: String,
        middleWares: List<KrutMiddleWare> = listOf(),
        handler: KrutHandler,
    ) {
        val route = buildRoute(
            method = KrutMethod.POST,
            path = path,
            handler = handler,
            middleWares = middleWares
        )

        routes.add(route)
    }

    fun put(
        path: String,
        middleWares: List<KrutMiddleWare> = listOf(),
        handler: KrutHandler,
    ) {
        val route = buildRoute(
            method = KrutMethod.PUT,
            path = path,
            handler = handler,
            middleWares = middleWares
        )

        routes.add(route)
    }

    fun delete(
        path: String,
        middleWares: List<KrutMiddleWare> = listOf(),
        handler: KrutHandler,
    ) {
        val route = buildRoute(
            method = KrutMethod.DELETE,
            path = path,
            handler = handler,
            middleWares = middleWares
        )

        routes.add(route)
    }

    private fun buildRoute(
        method: KrutMethod,
        path: String,
        handler: KrutHandler,
        middleWares: List<KrutMiddleWare>
    ): KrutRoute {
        val (pathRegex, paramNames) = compilePath(path)
        val fullHandler = chainMiddleWares(
            handler = handler,
            middleWares = globalMiddleWares + middleWares
        )

        return KrutRoute(
            method = method,
            path = path,
            pathRegex = pathRegex,
            paramNames = paramNames,
            handler = fullHandler
        )
    }

    fun listen(
        port: Int = 8080,
        host: String = "0.0.0.0",
        engineType: EngineType = EngineType.TOMCAT
    ) {
        val engine = when (engineType) {
            EngineType.HTTP_SERVER -> HttpServerEngine(
                port = port,
                host = host,
                handler = KrutHttpHandler(getRoutes = { routes })
            )

            EngineType.TOMCAT -> TomcatEngine(
                port = 8080,
                host = host,
                krutHandler = KrutServletHandler(
                    getRoutes = { routes }
                )
            )
        }

        println("Krut running at http://${if (host == "0.0.0.0") "localhost" else host}:$port")
        engine.start()
    }
}