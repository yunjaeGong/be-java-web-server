package service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TemplateService {
    /*
    1. 템플릿 읽기
    2. 주어진 태그 위치 찾기
    3. 위치에 쓰기
     */

    public static int findTag(StringBuilder sb, String tag, int pos) {
        int startingIdx = 0;
        int tagIdx = 0;

        for (int tries = 1; startingIdx < sb.length() && tries <= pos; tries++) {
            tagIdx = sb.indexOf(tag, startingIdx);
            startingIdx = tagIdx;
        }

        return tagIdx;
    }

    public static void replaceTag(StringBuilder sb, String with, int openingIdx, int len, String tag) {
        sb.replace(openingIdx + tag.length(), openingIdx + len, "");
        sb.insert(openingIdx + tag.length(), with);
    }

    public static void replaceWithString(StringBuilder sb, String toReplace, String with) {
        int idx = findTag(sb, toReplace, 0);
        sb.replace(idx, idx + toReplace.length(), "");
        sb.insert(idx, with);
    }

}
