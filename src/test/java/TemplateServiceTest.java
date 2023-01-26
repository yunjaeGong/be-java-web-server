import dto.Comment;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.TemplateService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static service.TemplateService.replaceStringWithGivenString;

public class TemplateServiceTest {

    @Test
    @DisplayName("생성된 페이지에서 toReplace String을 찾고, given 스트링으로 바꾸는 메서드 테스트")
    public void regeneratePageTest() throws IOException {
        SoftAssertions softly = new SoftAssertions();

        // given
        final StringBuilder template = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/templates/index.html"));
        String line = "";

        while ((line = br.readLine()) != null) {
            template.append(line);
        }

        List<Comment> comments = List.of(new Comment(1, "gildong", "gildong", LocalDateTime.now()),
                new Comment(2, "hong", "hong", LocalDateTime.now()));

        // when
        replaceStringWithGivenString(template, "로그인", "홍길동");
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i <= comments.size(); ++i) {
            Comment comment = comments.get(i - 1);
            String item = String.format(
                    "                        <th scope=\"row\"><span class=\"time\">%s</span></th>\n" +
                            "                        <td class=\"auth-info\">\n" +
                            "\n" +
                            "                            <a href=\"./user/profile.html\" class=\"author\">%s</a>\n" +
                            "                        </td>\n" +
                            "                        <td class=\"body\">\n" +
                            "                            <span>%s</span>\n" +
                            "                        </td>\n", comment.getCreateDate(), comment.getUserName(), comment.getBody());

            sb.append("<tr>");
            sb.append(item);
            sb.append("        </tr>");
        }

        replaceStringWithGivenString(template, "<!-- %comment% -->", sb.toString());


        softly.assertThat(template.toString()).doesNotContain("<!-- %comment% -->").doesNotContain("로그인");
    }
}
