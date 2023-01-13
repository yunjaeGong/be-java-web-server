package dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.HttpStatusCode;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    public final HttpStatusCode statusCode;
    public String contentType;
    public final String resourcePath;

    private Map<String, String> header;
            
    public HttpResponse(String resourcePath, HttpStatusCode statusCode, Map<String, String> header, String contentType) {
        this.statusCode = statusCode;
        this.resourcePath = resourcePath;
        this.header = header;
        this.contentType = contentType;  // TODO: contentType = null일 때 e.g., woff 예외처리 필요
    }

    public HttpResponse(String resourcePath, HttpStatusCode statusCode, String contentType) {
        this(resourcePath, statusCode, new HashMap<>(), contentType);
    }

    public DataOutputStream of(DataOutputStream dos) throws IOException {
        byte[] body = "".getBytes();

        if(!resourcePath.isBlank()) {
            try {
                body = Files.readAllBytes(new File(resourcePath).toPath());
            } catch (IOException e) {
                logger.error("cannot open file: " + e.getMessage());
            }
        }

        dos.writeBytes(String.format("%s %s \r\n", "HTTP/1.1", statusCode.toString()));
        dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8 \r\n");
        dos.writeBytes("Content-Length: " + body.length + " \r\n");

        // StatusCode specific header
        for(Map.Entry<String, String> header : header.entrySet()) {
            String key = header.getKey();
            String val = header.getValue();

            dos.writeBytes(key + ": " + val + " \r\n");
        }
        dos.writeBytes("\r\n");

        dos.write(body, 0, body.length);

        return dos;
    }
}
