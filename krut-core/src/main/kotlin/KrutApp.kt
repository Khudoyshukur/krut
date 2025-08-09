import com.sun.net.httpserver.HttpServer
import engine.EngineType
import engine.HttpServerEngine
import engine.TomcatEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import network.HttpExchangeHandler
import java.net.InetSocketAddress

class KrutApp(
    private val globalMiddlewares: List<KrutMiddleware> = listOf()
) {

    private val routes = mutableListOf<KrutRoute>()

    fun get(
        path: String,
        middlewares: List<KrutMiddleware> = listOf(),
        handler: KrutHandler
    ) {
        val route = buildRoute(method = KrutMethod.GET, path, middlewares, handler)
        routes.add(route)
    }

    fun post(
        path: String,
        middlewares: List<KrutMiddleware> = listOf(),
        handler: KrutHandler
    ) {
        val route = buildRoute(method = KrutMethod.POST, path, middlewares, handler)
        routes.add(route)
    }

    fun put(
        path: String,
        middlewares: List<KrutMiddleware> = listOf(),
        handler: KrutHandler
    ) {
        val route = buildRoute(method = KrutMethod.PUT, path, middlewares, handler)
        routes.add(route)
    }

    fun delete(
        path: String,
        middlewares: List<KrutMiddleware> = listOf(),
        handler: KrutHandler
    ) {
        val route = buildRoute(method = KrutMethod.DELETE, path, middlewares, handler)
        routes.add(route)
    }

    private fun buildRoute(
        method: KrutMethod,
        path: String,
        middlewares: List<KrutMiddleware> = listOf(),
        handler: KrutHandler
    ): KrutRoute {
        val formattedPath = path.removeSuffix("/")
        val (regex, pathNames) = getRegexAndPathNames(formattedPath)

        val newHandler = chainMiddlewares(handler, globalMiddlewares + middlewares)

        return KrutRoute(
            method = method,
            path = formattedPath,
            handler = newHandler,
            pathRegex = regex,
            pathNames = pathNames
        )
    }


    fun listen(
        port: Int,
        host: String,
        engineType: EngineType = EngineType.TOMCAT
    ) {
        val engine = when(engineType) {
            EngineType.HTTP_SERVER -> HttpServerEngine(host = host, port = port, routes = { routes })
            EngineType.TOMCAT -> TomcatEngine(port = port, host = host, routes = { routes })
        }

        println("Engine started at http://localhost:$port using engine=${engineType}")

        engine.start()
    }
}