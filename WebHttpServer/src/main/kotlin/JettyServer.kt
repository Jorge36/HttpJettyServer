package org.example

import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory
import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.ContextHandlerCollection


class JettyServer (private val serverPort: Int) {

    fun startServer() {
        val server = Server()
        // The HTTP configuration object.
        val httpConfig = HttpConfiguration()
        // The ConnectionFactory for clear-text HTTP/2.
        val http2c = HTTP2CServerConnectionFactory(httpConfig)
        // The ConnectionFactory for HTTP/1.1.
        val http11 = HttpConnectionFactory()
        // The ServerConnector instance.
        val connector = ServerConnector(server, http11, http2c)
        connector.port = serverPort
        server.connectors = arrayOf(connector)


        // Create a ContextHandlerCollection to hold contexts.
        val contextCollection = ContextHandlerCollection()
        val statusHandler = ContextHandler(NonBlockingStatus(), "/status")
        statusHandler.allowNullPathInContext = true

        val taskHandler = ContextHandler(NonBlockingTask(), "/task")
        taskHandler.allowNullPathInContext = true

        contextCollection.addHandler(statusHandler)
        contextCollection.addHandler(taskHandler)

        // Link the ContextHandlerCollection to the Server.
        server.handler = contextCollection

        //server.handler = ContextHandler(NonBlockingStatus(), "/status")
        server.start()
    }
}