package JettyWebServer

import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory
import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector

fun main(args: Array<String>) {
    var serverPort = 8081
    if (args.size == 1) {
        serverPort = args[0].toInt()
    }

    val webServer: JettyServer = JettyServer(serverPort)
    webServer.startServer()
    println("Jetty - Server is listening on port $serverPort")
}

class JettyServer(val serverPort: Int) {

    fun startServer() {
        val server: Server = Server()
        // The HTTP configuration object.
        val httpConfig = HttpConfiguration()
        // The ConnectionFactory for clear-text HTTP/2.
        val http2c: HTTP2CServerConnectionFactory = HTTP2CServerConnectionFactory(httpConfig)
        // The ConnectionFactory for HTTP/1.1.
        val http11: HttpConnectionFactory = HttpConnectionFactory()
        // The ServerConnector instance.
        val connector : ServerConnector = ServerConnector(server, http11, http2c)
        connector.port = serverPort
        server.connectors = arrayOf(connector)
        server.setHandler(BlockingServletStatus())
        server.start()
    }

}