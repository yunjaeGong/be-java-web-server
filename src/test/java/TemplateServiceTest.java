import org.junit.jupiter.api.Test;
import service.TemplateService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TemplateServiceTest {

    @Test
    public void Given_StringToFind_When_findTag_ThenSuccess() throws IOException {
        String body = Files.readString(new File("src/main/resources/templates/index.html").toPath());

        StringBuilder sb = new StringBuilder(body);

        int idx = TemplateService.findTag(sb, "<!DOCTYPE", 1);
        assertThat(sb.indexOf("<!DOCTYPE", idx)).isZero();
    }

    @Test
    public void Given_StringToFind_When_findSecondTag_ThenSuccess() throws IOException {
        // given
        String body = Files.readString(new File("src/main/resources/templates/index.html").toPath());
        StringBuilder sb = new StringBuilder(body);

        // when
        int idx = TemplateService.findTag(sb, "로그인", 2);

        // then
        int expectedIdx = sb.indexOf("로그인", 0);
        expectedIdx = sb.indexOf("로그인", expectedIdx);

        assertThat(idx).isEqualTo(expectedIdx);
    }


    @Test
    public void Given_StringToReplace_When_replaceWithString_ThenReplace() throws IOException {
        String body = Files.readString(new File("src/main/resources/templates/index.html").toPath());

        StringBuilder sb = new StringBuilder(body);


        TemplateService.replaceWithString(sb, "로그인", "홍길동동동동동동");
        assertThat(sb.indexOf("홍길동동동동동동")).isNotNegative();
    }

    @Test
    public void Given_StringToReplace_When_replaceStringWithTags_ThenReplace() throws IOException {
        String body = Files.readString(new File("src/main/resources/templates/user/list.html").toPath());

        StringBuilder sb = new StringBuilder(body);
        int openingIdx = TemplateService.findTag(sb, "<tbody>", 1);
        int closingIdx = TemplateService.findTag(sb, "</tbody>", 1);
        String toReplace = "<tr>\n" +
                "                    <th scope=\"row\">1</th> <td>javajigi</td> <td>자바지기</td> <td>javajigi@sample.net</td><td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>\n" +
                "                </tr>";
        TemplateService.replaceTag(sb, toReplace, openingIdx, closingIdx-openingIdx, "<tbody>");
        System.out.println(sb);
    }
}
