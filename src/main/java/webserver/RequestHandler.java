package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final String DEFAULT_PATH = "/Users/rentalhub-mac88/Desktop/Softeer/be-java-web-server/src/main/resources/";
    private static final String TEMPLATES_PATH = "/templates";
    private static final String STATIC_PATH = "/static";
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        byte[] body = null;
        FileInputStream uri = null;
        StringBuilder resourcePath = new StringBuilder(DEFAULT_PATH);

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String[] header = br.readLine().split(" ");

            String methodType = header[0];
            String path = header[1];  // TODO: QuerySting parameter parsing 기능 추가
            String httpVersion = header[2];
            logger.debug("request path: " + path);

            if(path.contains("html")) {
                resourcePath.append(TEMPLATES_PATH);
            } else {
                resourcePath.append(STATIC_PATH);
            }

            resourcePath.append(path);

            // TODO: Controller 구현
            try {
                body = Files.readAllBytes(new File(resourcePath.toString()).toPath());
            } catch (FileNotFoundException e) {
                logger.error("Error while reading requested uri", e);
            }

            DataOutputStream dos = new DataOutputStream(out);

            assert body!= null;

            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
