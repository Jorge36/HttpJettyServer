package org.example

import org.eclipse.jetty.http.HttpHeader
import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.io.Content
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response
import org.eclipse.jetty.util.Callback

class NonBlockingStatus: Handler.Abstract.NonBlocking() {

        override fun handle(request: Request?, response: Response?, callback: Callback?): Boolean {

        if (request == null || response == null || callback == null) {
            println("entro")
            return false
        }

        if (!(request.method.equals("get", true))) {            // Send an error response, completing the callback, and returning true.
            response.status = HttpStatus.BAD_REQUEST_400
            response.headers.put(HttpHeader.CONTENT_TYPE, "text/plain; charset=UTF-8")
            Content.Sink.write(response, true, "Invalid request!", callback)
            return true
        }

        response.status = HttpStatus.OK_200
        response.headers.put(HttpHeader.CONTENT_TYPE, "text/plain; charset=UTF-8")
        Content.Sink.write(response, true, "Server is alive!" + System.lineSeparator(), callback)
        return true
    }
}