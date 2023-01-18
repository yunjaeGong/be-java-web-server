package utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public enum HttpMethodType {
    GET(1),
    POST(2);

    private final int methodType;

    HttpMethodType(int methodType) {
        this.methodType = methodType;
    }
}
