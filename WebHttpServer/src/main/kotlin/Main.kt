package org.example

fun main(args: Array<String>) {
    var serverPort = 8080
    if (args.size == 1) {
        serverPort = args[0].toInt()
    }

    val webServer = JettyServer(serverPort)
    webServer.startServer()
    println("Kotlin - Server is listening on port $serverPort")
}