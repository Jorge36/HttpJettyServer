# HttpJettyServer
Http Server using Jetty 12 which exposes two endpoints /status and /task. Technologies I used to code this application are:

1. Oracle OpenJDK and JRE 22.0.1
2. Kotlin PL (language version 1.9, API version 1.9 and target platform JVM 1.8)
3. Maven 3.9.6
4. Intellij IDEA 2024.1.1 (Community Edition)
5. Jetty 12 (Embedded Jetty with Maven)

In this repository, there are also a HTTP Server and a HTTP Client applications which are taken from the course [Distributed Systems & Cloud Computing with Java](https://www.udemy.com/course/distributed-systems-cloud-computing-with-java/). The Http Server, which I coded using Jetty 12 and Kotlin, exposes the same endpoints as the one provided by Michael Pogrebinsky (creator of the course). In other words, they are identical regarding functionalities, but they used different technologies.

I used curl (command line tool), Postman and Http Client (provided in the course) to test the Http Server. Also Wireshark to capture localhost traffic capture.

The Http Server and client provided in the course use JDK libraries for the development. JDK built-in Http Client support HTTP/2 and HTTP/1.1 connection pooling by default. 
OpenJDK and JRE 11, Maven 3.9.6 were used to compile, install and run these two applications. For Http1.1, we need to delay the second request to reuse the same connection -to receive the response before sending the next request-, otherwise the connection is closed and a new one is created to send a second request (piece of code below shows it).

```
    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks) {
        CompletableFuture<String>[] futures = new CompletableFuture[workersAddresses.size()];

        for (int i = 0; i < workersAddresses.size(); i++) {
            String workerAddress = workersAddresses.get(i);
            String task = tasks.get(i);

            byte[] requestPayload = task.getBytes();
            futures[i] = webClient.sendTask(workerAddress, requestPayload);

            try {
                Thread.sleep(2000); // The main thread sleeps for the 2000 milliseconds, which is 2 sec
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
 ```

On the other hand, the application coded with Jetty 12 supports clear HTTP/1.1 and HTTP/2. Jetty will start parsing the incoming bytes as HTTP/1.1, but then realize that they are HTTP/2 bytes and will therefore upgrade from HTTP/1.1 to HTTP/2.

 ```
        val server = Server()
        // The HTTP configuration object.
        val httpConfig = HttpConfiguration()
        // The ConnectionFactory for clear-text HTTP/2.
        val http2c = HTTP2CServerConnectionFactory(httpConfig)
        // The ConnectionFactory for HTTP/1.1.
        val http11 = HttpConnectionFactory()
        // The ServerConnector instance.
        val connector = ServerConnector(server, http11, http2c)
 ```
To use encrypted protocols, configure the SslContextFactory with the keyStore information that has access to the KeyStore containing the private server key and public server certificate. 

Below I added screenshots of tests carried out.

**Http Server with JDK 11**


Endpoint does not exist<br>
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/1195469bcb40563a8bdd071423bbb857e9a5435b/testing/jdk/get%20request%20404.png)


![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/1195469bcb40563a8bdd071423bbb857e9a5435b/testing/jdk/get%20request%20status%20200.png)

![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/1195469bcb40563a8bdd071423bbb857e9a5435b/testing/jdk/post%20request%20task%20200.png)

![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/1195469bcb40563a8bdd071423bbb857e9a5435b/testing/jdk/post%20request%20test%20200.png)

Server does not support HTTP2, therefore protocol is not upgraded
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/1195469bcb40563a8bdd071423bbb857e9a5435b/testing/jdk/http1%20upgrading%20to%20http2.png)

Server does not support HTTP2, empty response
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/1195469bcb40563a8bdd071423bbb857e9a5435b/testing/jdk/enable%20use%20of%20HTTP2%20without%20HTTP1.1%20Upgrade.png)

**Http Server with Jetty 12**


mvn clean compile command to compile source code
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/1195469bcb40563a8bdd071423bbb857e9a5435b/testing/jetty/jetty%20application%20compiled%20with%20maven.png)

Running Http Server<br>
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/1195469bcb40563a8bdd071423bbb857e9a5435b/testing/jetty/listening%20in%20port%208080.png)

Endpoint does not exist
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/1195469bcb40563a8bdd071423bbb857e9a5435b/testing/jetty/get%20request%20404.png)

![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/1195469bcb40563a8bdd071423bbb857e9a5435b/testing/jetty/get%20request%20200.png)

Protocol is upgraded, because it is a get request
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/f5d1a31f636851b33b70e04667e840dc6298996b/testing/jetty/get%20request%20upgrading%20to%20http2.png)

Protocol used is HTTP/2
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/f5d1a31f636851b33b70e04667e840dc6298996b/testing/jetty/get%20request%20enable%20use%20of%20HTTP2%20without%20HTTP1%20upgrading.png)

![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/1195469bcb40563a8bdd071423bbb857e9a5435b/testing/jetty/post%20request%20debug%20task.png)

![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/f5d1a31f636851b33b70e04667e840dc6298996b/testing/jetty/post%20request%20test%20task.png)

Protocol is not upgraded, because it is a post request
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/f5d1a31f636851b33b70e04667e840dc6298996b/testing/jetty/post%20request%20upgrading%20to%20http2.png)

Protocol used is HTTP/2
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/f5d1a31f636851b33b70e04667e840dc6298996b/testing/jetty/post%20request%20enable%20use%20of%20HTTP2%20without%20HTTP1%20upgrading.png)

**Postman**

Get request
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/f5d1a31f636851b33b70e04667e840dc6298996b/testing/postman/get%20request%20status%20postman.png)

Post request
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/f5d1a31f636851b33b70e04667e840dc6298996b/testing/postman/post%20request%20header%20postman.png)

![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/f5d1a31f636851b33b70e04667e840dc6298996b/testing/postman/post%20request%20body%20postman.jpg)

![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/f5d1a31f636851b33b70e04667e840dc6298996b/testing/postman/post%20request%20response%20header%20postman.png)

![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/f5d1a31f636851b33b70e04667e840dc6298996b/testing/postman/post%20request%20result%20postman.png)

**Wireshark running Http Server with Jetty 12**

Post request ask to upgrade to HTTP/2
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/34a14ef16f280a0fe9be076dd7e570add4cfa2a5/testing/http2/request%20post%20to%20enable%20use%20of%20http2.png)

Upgrade header inviting the server to upgrade
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/34a14ef16f280a0fe9be076dd7e570add4cfa2a5/testing/http2/http1%20upgrade%20http2.png)

Upgrade is not accepted, server response uses HTTP/1.1
![alt_text](https://github.com/Jorge36/HttpJettyServer/blob/34a14ef16f280a0fe9be076dd7e570add4cfa2a5/testing/http2/result%20http1%20upgrade%20http2.png)

There are three videos on https://drive.google.com/drive/folders/1sx8MxSMudYnvIJ94m2aRc1QpFMVA7R9v?usp=sharing

1. enable use of HTTP2 without HTTP1.1 Upgrade.mkv: post request to Http Server with Jetty 12 using HTTP/2.
2. post request jdk client - jdk server.mkv: Testing http server with JDK 11 using client application provided in the course
3. post request jdk client - jetty server.mkv: Testing http server with Jetty 12 using client application provided in the course

Additional Information:
1. https://jetty.org/
2. https://curl.se/docs/http2.html
3. https://www.rfc-editor.org/rfc/rfc7540?msclkid=198be25fc87b11eca21146cdba8d08f5#section-3.2
4. https://github.com/jetty/jetty.project/issues/11588 (to understand why only get requests are upgraded, switching protocols)

