package org.example

import org.eclipse.jetty.http.HttpHeader
import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.io.Content
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response
import org.eclipse.jetty.util.Callback
import java.math.BigInteger


class NonBlockingTask: Handler.Abstract.NonBlocking() {

    override fun handle(request: Request?, response: Response?, callback: Callback?): Boolean {

        if (request == null || response == null || callback == null) {
            return false
        }

        if (!(request.method.equals("post", true))) {            // Send an error response, completing the callback, and returning true.
            response.status = HttpStatus.BAD_REQUEST_400
            response.headers.put(HttpHeader.CONTENT_TYPE, "text/plain; charset=UTF-8")
            Content.Sink.write(response, true, "Invalid request!", callback)
            return true
        }

        val headers = request.headers
        if (headers.contains("X-Test") && headers.getField("X-Test").value.equals("true", true)) {
            response.status = 200
            response.headers.put(HttpHeader.CONTENT_TYPE, "text/plain; charset=UTF-8")
            Content.Sink.write(response, true, "123" + System.lineSeparator(), callback)
            return true
        }

        var isDebugMode = false
        if (headers.contains("X-Debug") && headers.getField("X-Debug").value.equals("true", true)) {
            isDebugMode = true

        }
        val startTime = System.nanoTime()

        val completable = Content.Source.asString(request)
        var result = calculateResponse(completable.toString()) + System.lineSeparator()
        response.status = HttpStatus.OK_200
        response.headers.put(HttpHeader.CONTENT_TYPE, "text/plain; charset=UTF-8")

        val finishTIme = System.nanoTime()

        if (isDebugMode) {
            response.headers.put("X-Debug-Info", "Operation took ${finishTIme - startTime} ns")
        }
        Content.Sink.write(response, true, result, callback)
        return true

    }

    private fun calculateResponse(bodyString: String?): String {

        if (bodyString == null) {
            return String()
        }

        val stringNumbers = bodyString.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()

        var result = BigInteger.ONE

        for (number in stringNumbers) {
            val bigInteger = BigInteger(number)
            result = result.multiply(bigInteger)
        }

        return "Result of the multiplication is $result"

    }


}