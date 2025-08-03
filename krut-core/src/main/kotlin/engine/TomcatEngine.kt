package engine

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import network.KrutTomcatHandler
import org.apache.catalina.connector.Connector
import org.apache.catalina.startup.Tomcat

class TomcatEngine(
    private val port: Int = 8080,
    private val host: String,
    private val contextPath: String = "",
    private val krutHandler: KrutTomcatHandler
): KrutEngine {

    private val tomcat = Tomcat()

    override fun start() {
        tomcat.setPort(port)
        val ctx = tomcat.addContext(contextPath, System.getProperty("java.io.tmpdir"))

        Tomcat.addServlet(ctx, "krutServlet", object : HttpServlet() {
            override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
                krutHandler.handle(req, resp)
            }
        })
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
    }
}