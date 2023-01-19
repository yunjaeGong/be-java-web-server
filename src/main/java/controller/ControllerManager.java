package controller;

import db.Database;
import dto.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.HttpMethodType;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class ControllerManager {

    private static final Logger logger = LoggerFactory.getLogger(ControllerManager.class);

    private static Map<Class<?>, Object> instance;
    private static List<Class<?>> controllers;

    static {
        instance = Map.ofEntries(
                entry(UserController.class, new UserController()),
                entry(ViewController.class, new ViewController())
        );
        controllers = List.of(UserController.class, ViewController.class);
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, IllegalAccessException {
        String request = "GET /index.html HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n" +
                "Connection: keep-alive \r\n" +
                "Accept: */* \r\n";

        // when
        HttpRequest req = new HttpRequest(new ByteArrayInputStream(request.getBytes()));

        for(Class con : controllers) {
            List<Method> methods = Arrays.stream(con.getMethods()).filter(m -> m.isAnnotationPresent(ControllerMapping.class)).collect(Collectors.toList());
            Method method = null;
            Object controllerInstance = instance.get(con);

            method = methods.stream().filter((m)->ifMatchController(m, req)).findFirst().orElse(null);

            if(method == null)
                continue;

            try {
                method.invoke(controllerInstance, req);  // TODO: Controller 인스턴스 주입
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NullPointerException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private static boolean ifMatchController(Method method, HttpRequest req) {
        ControllerMapping a = method.getAnnotation(ControllerMapping.class);
        return (a.method() == HttpMethodType.valueOf(req.getMethodType()) && a.path().equals(req.getPath()));
    }
}
