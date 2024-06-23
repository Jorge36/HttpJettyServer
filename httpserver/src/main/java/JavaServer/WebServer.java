package JavaServer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class WebServer {

    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";

    private final int port;

    public static void main(String []args) {

        int serverPort = 8081;
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }

        WebServer webServer = new WebServer(serverPort);
        webServer.startSever();

        System.out.println("Java - Server is listening on port " + serverPort);

    }

    public WebServer(int port) {
        this.port = port;
    }

    public void startSever() {

        HttpServer server;
        try {
            // create a socket address with port equal to the attribute port.
            // backlog parameter specifies the number of pending connections the queue will hold.
            //  If this value is less than or equal to zero, then a system default value is used.
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // create context without specifying the handler for the endpoint status
        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        // create context without specifying the handler for the endpoint task
        HttpContext textContext = server.createContext(TASK_ENDPOINT);

        // set handlers for statis and task
        statusContext.setHandler(this::handleStatusCheckRequest);
        textContext.setHandler(this::handleTaskRequest);

        // start the server with a fixed number of threads to process the tasks
        server.setExecutor(Executors.newFixedThreadPool(8));
        // start the server
        server.start();
    }

    // handle the request
    private void handleTaskRequest(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }

        Headers headers = exchange.getRequestHeaders();
        if (headers.containsKey("X-Test") && headers.get("X-Test").get(0).equalsIgnoreCase("true")) {
            String dummyResponse = "123" + System.lineSeparator();
            sendResponse(dummyResponse.getBytes(),exchange);
            return;
        }

        boolean isDebugMode = false;
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")) {
            isDebugMode = true;
        }

        long startTime = System.nanoTime();

        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        byte[] responseBytes = calculateResponse(requestBytes);

        long finishTIme = System.nanoTime();

        if (isDebugMode) {

            String debugMessage = String.format("Operation took %d ns", finishTIme - startTime);
            String[] messages = new String[] {debugMessage};
            exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(messages));

        }

        sendResponse(responseBytes, exchange);
    }

    private byte[] calculateResponse(byte[] requestBytes) {
        String bodyString = new String(requestBytes);

        String[] stringNumbers = bodyString.split(",");

        BigInteger result = BigInteger.ONE;

        for (String number: stringNumbers) {
            BigInteger bigInteger = new BigInteger(number);
            result = result.multiply(bigInteger);
        }

        return String.format("Result of the multiplication is %s\n", result).getBytes();

    }

    // handle the request for status
    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {

        // if method is not get , ends the request
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        String responseMessage = "Server is alive!";
        // send response to client
        sendResponse(responseMessage.getBytes(), exchange);

    }

    private void sendResponse(byte[] bytes, HttpExchange exchange) throws IOException {

        // set response header: status response code and length of the response
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

}
