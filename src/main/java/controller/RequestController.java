package controller;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.HttpRequestParser;
import dto.HttpResponse;
import utility.HttpStatusCode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class RequestController {
    /*
    경로에 따라 정적 리소스, 동적 리소스 구분
    정적 리소스는 단순 서빙
    동적 리소스의 경우 Redirect, 비즈니스 로직 처리 등 수행
    */
    public static final String DEFAULT_PATH = "./src/main/resources";
    public static final String TEMPLATES_PATH = "/templates";
    public static final String STATIC_PATH = "/static";
    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    private static final int DYNAMIC = 1;
    private static final int STATIC = 2;

    private static final List<String> staticResource = new ArrayList<>(Arrays.asList("js", "html", "css", "font", "woff"));

    public Map<String, Integer> resources;

    private static RequestController controller = null;

    private Map<String, String> route;
    private RequestController() {
        this.route = new HashMap<>();
        resources = new HashMap<>();
        resources = staticResource.stream().collect(Collectors.toMap(str -> str, str -> STATIC));
    }

    public static RequestController getInstance() {
        if(controller == null) {
            controller = new RequestController();
        }
        return controller;
    }

    // TODO: HttpResponse 반환하게
    public static HttpResponse requestController(String resourcePath) throws IOException {
        HttpRequestParser requestParser = new HttpRequestParser(resourcePath);
        String path = requestParser.path;

        String[] args = path.split("\\.");

        if(args.length >= STATIC)  // .min.js, .js, .html , .ico
            return staticResourceController(requestParser.path);
        else
            return dynamicResourceController(requestParser.path, requestParser);

    }

    public static HttpResponse requestController(InputStream in) throws IOException {
        HttpRequestParser requestParser = new HttpRequestParser(in);
        String path = requestParser.path;

        logger.debug("Request Path: " + path);

        String[] args = path.split("\\.");

        if(args.length >= STATIC)  // .min.js, .js, .html , .ico
            return staticResourceController(requestParser.path);
        else
            return dynamicResourceController(requestParser.path, requestParser);

    }

    // TODO: HttpResponse 반환하게
    public static HttpResponse staticResourceController(String path) throws IOException {
        StringBuilder resourcePath = new StringBuilder(DEFAULT_PATH);

        if(path.contains("html") || path.contains(".ico")) {
            resourcePath.append(TEMPLATES_PATH);
        } else {
            resourcePath.append(STATIC_PATH);
        }
        resourcePath.append(path);

        String contentType = Files.probeContentType(Path.of(path));
        logger.debug("contentType: " +contentType);

        return new HttpResponse(resourcePath.toString(), HttpStatusCode.OK, contentType);
    }

    public static HttpResponse dynamicResourceController(String path, HttpRequestParser parser) throws IOException {
        // HttpResponse response = NotFoundResponse;
        Map<String, String> header = new HashMap<>();
        HttpStatusCode status = HttpStatusCode.NOT_FOUND;

        if(path.equals("/")) {
            path = "/index.html";
            status = HttpStatusCode.OK;
        }

        if(path.contains("/create") && parser.hasParams()) {
            Map<String, String> params = parser.getParams();

            User user = User.UserBuilder.builder()
                    .setUserId(params.get("userId"))
                    .setPassword(params.get("password"))
                    .setName(params.get("name"))
                    .setEmail(params.get("email")).build();

            // TODO: UserService로 분리
            Database.addUser(user);

            logger.debug("created User: {}", Database.findUserById(params.get("userId")));

            path = "";
            header.put("Location", "/index.html");
        }

        String contentType = Files.probeContentType(Path.of(path));
        logger.debug("contentType: " +contentType);

        return new HttpResponse(path, status, header, contentType);
    }
}
