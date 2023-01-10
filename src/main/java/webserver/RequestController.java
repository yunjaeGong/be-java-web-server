package webserver;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.HttpRequestParser;
import utility.HttpResponse;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RequestController {
    /*
    경로에 따라 정적 리소스, 동적 리소스 구분
    정적 리소스는 단순 서빙
    동적 리소스의 경우 Redirect, 비즈니스 로직 처리 등 수행
    */

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    public static final String DEFAULT_PATH = "./src/main/resources";
    public static final String TEMPLATES_PATH = "/templates";
    public static final String STATIC_PATH = "/static";

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
    public static String requestController(String resourcePath) throws IOException {
        HttpRequestParser requestParser = new HttpRequestParser(resourcePath);
        String path = requestParser.path;

        String[] args = path.split("\\.");

        if(args.length >= STATIC)  // .min.js, .js, .html , .ico
            return staticResourceController(requestParser.path);
        else
            return dynamicResourceController(requestParser.path, requestParser);

    }

    // TODO: HttpResponse 반환하게
    public static String staticResourceController(String path) {
        StringBuilder resourcePath = new StringBuilder(DEFAULT_PATH);

        if(path.contains("html") || path.contains(".ico")) {
            resourcePath.append(TEMPLATES_PATH);
        } else {
            resourcePath.append(STATIC_PATH);
        }
        resourcePath.append(path);

        return resourcePath.toString();
    }

    public static String dynamicResourceController(String path, HttpRequestParser parser) throws IOException {


        if(path.equals("/"))
            path = "/index.html"; // TODO: Redirect Response 보내기

        if(path.contains("/create") && parser.hasParams()) {
            Map<String, String> params = parser.getParams();

            User user = User.UserBuilder.builder()
                    .setUserId(params.get("userId"))
                    .setPassword(params.get("password"))
                    .setName(params.get("name"))
                    .setEmail(params.get("email")).build();

            Database.addUser(user);
            logger.debug("created User: {}", Database.findUserById(params.get("userId")));
            // TODO: Redirect Response 보내기
        }

        return "dynamic";  // TODO: HttpResponse 반환
    }
}
