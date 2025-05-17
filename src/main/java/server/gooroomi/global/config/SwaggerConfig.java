package server.gooroomi.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String title = "🚌 구루미 API";
        String description = """
                구루미 프로젝트 API 문서입니다.

                ### 구루미 프로젝트
                구루미 애플리케이션의 백엔드 API입니다.

                ### 응답 코드
                - 200: 요청 성공
                - 400: 잘못된 요청
                - 404: 리소스 없음
                - 500: 서버 오류
                """;

        // API 정보 설정
        Info info = new Info().title(title).description(description).version("v1.0.0");

        return new OpenAPI().info(info);
    }
}
