package utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            
    public HttpResponse(String resourcePath, HttpStatusCode statusCode, Map<String, String> header) {
        this.statusCode = statusCode;
        this.resourcePath = resourcePath;
        this.header = header;
    }

    public HttpResponse(String resourcePath, HttpStatusCode statusCode) {
        this(resourcePath, statusCode, new HashMap<>());
    }

    public String of() throws IOException {
        final String NEXT_LINE = " \r\n";
        byte[] body = "".getBytes();

        if(!resourcePath.isBlank()) {
            try {
                body = Files.readAllBytes(new File(resourcePath.toString()).toPath());
            } catch (IOException e) {
                logger.error("cannot open file: " + e.getMessage());
            }
        }

        StringBuilder sb = new StringBuilder(String.format("%s %s \r\n", "HTTP/1.1", statusCode.toString()));
        sb.append("Content-Type: text/" + contentType + ";charset=utf-8 \r\n");
        sb.append("Content-Length: " + body.length + " \r\n");

        // StatusCode specific header
        for(Map.Entry<String, String> header : header.entrySet()) {
            String key = header.getKey();
            String val = header.getValue();

            sb.append(key).append(": ").append(val).append(" \r\n");
        }

        responseBody(body, sb);

        return sb.toString();
    }

    private void responseBody(byte[] body, StringBuilder sb) {
        sb.append(body);
    }
}
