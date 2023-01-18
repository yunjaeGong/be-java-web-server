import org.junit.jupiter.api.Test;
import service.TemplateService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TemplateServiceTest {

    @Test
    public void Given_StringToReplace_When_replaceString_ThenReplace() throws IOException {
        String body = Files.readString(new File("src/main/resources/templates/index.html").toPath());

        StringBuilder sb = new StringBuilder(body);


        TemplateService.replaceWithString(sb, "로그인", "홍길동동동동동동");
        assertThat(sb.indexOf("홍길동동동동동동")).isNotNegative();
    }
}
