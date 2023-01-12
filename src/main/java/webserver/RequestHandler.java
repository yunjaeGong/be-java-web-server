package webserver;

import java.io.*;
import java.net.Socket;

import controller.RequestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dto.HttpResponse;

public class RequestHandler implements Runnable {

    private static final String DEFAULT_PATH = "/Users/rentalhub-mac88/Desktop/Softeer/be-java-web-server/src/main/resources";
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

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {  // try with resource

            DataOutputStream dos = new DataOutputStream(out);

            HttpResponse response = RequestController.requestController(in);
            dos = response.of(dos);

            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
