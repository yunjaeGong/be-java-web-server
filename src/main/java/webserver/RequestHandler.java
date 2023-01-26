package webserver;

import java.io.*;
import java.net.Socket;

import controller.RequestDispatcher;
import dto.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dto.HttpResponse;

public class RequestHandler implements Runnable {
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

            HttpRequest request = new HttpRequest(in);
            HttpResponse response = RequestDispatcher.fulfillRequest(request);
            dos = response.of(dos);

            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
