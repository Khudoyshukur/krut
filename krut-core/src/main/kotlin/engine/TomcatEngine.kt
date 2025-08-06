package engine

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import network.KrutServletHandler
import org.apache.catalina.connector.Connector
import org.apache.catalina.startup.Tomcat
import java.util.concurrent.Executors

class TomcatEngine(
    private val port: Int = 8080,
    private val host: String,
    private val contextPath: String = "",
    private val krutHandler: KrutServletHandler
): KrutEngine {
    private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private val tomcat = Tomcat()

    override fun start() {
        tomcat.setPort(port)
        val ctx = tomcat.addContext(contextPath, System.getProperty("java.io.tmpdir"))

        Tomcat.addServlet(ctx, "krutServlet", object : HttpServlet() {
            override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
                val async = req.startAsync()
                scope.launch {
                    krutHandler.handle(req, resp)
                    async.complete()
                }
            }
        }).apply { isAsyncSupported = true }
        ctx.addServletMappingDecoded("/*", "krutServlet")

        val connector = Connector()
        connector.port = port
        connector.setProperty("address", host)
        tomcat.service.addConnector(connector)
        tomcat.connector = connector

        tomcat.start()
        tomcat.server.await()
    }

    override fun stop() {
        tomcat.stop()
        scope.cancel()
        dispatcher.close()
    }
}