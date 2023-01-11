package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.HttpRequestParser;

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

        byte[] body = null;
        FileInputStream uri = null;
        StringBuilder resourcePath = new StringBuilder(DEFAULT_PATH);

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {  // try with resource
            HttpRequestParser requestParser = new HttpRequestParser(in);

            String path = requestParser.path;

            logger.debug("request path: " + path);

            // TODO: Controller 클래스로 아래 정적 리소스 라우팅 및 byte[] 뷰 반환 기능 위임
            if(path.equals("/"))
                path = "/index.html";

            if(path.contains("/create") && requestParser.hasParams()) {
                Map<String, String> params = requestParser.getParams();

                User user = User.UserBuilder.builder()
                        .setUserId(params.get("userId"))
                        .setPassword(params.get("password"))
                        .setName(params.get("name"))
                        .setEmail(params.get("email")).build();

                Database.addUser(user);
                logger.debug("created User: {}", Database.findUserById(params.get("userId")));
                // TODO: redirect
            }

            if(path.split("\\.").length >= 2) {
                if (path.contains(".html") || path.contains(".ico")) {
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
            }

            // end of controller

            DataOutputStream dos = new DataOutputStream(out);

            assert body!= null;

            if(path.contains("/create") && requestParser.hasParams()) {
                response303Header(dos, 0, getContentType(path));
            } else {
                response200Header(dos, body.length, getContentType(path));
            }
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String getContentType(String path) {
        String[] types = path.split("\\.");
        String type = types[types.length-1];

        if(type.equals("js")) {
            type = "javascript";
        }

        return type;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/" + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response303Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 303 SEE OTHER \r\n");
            dos.writeBytes("Location: /index.html \r\n");
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
