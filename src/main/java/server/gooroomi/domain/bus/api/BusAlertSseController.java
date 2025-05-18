package server.gooroomi.domain.bus.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import server.gooroomi.domain.bus.application.SseEmitterService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
@Tag(name = "버스 도착 알림", description = "SSE를 통해 사용자에게 원하는 버스 도착 정보를 실시간으로 제공합니다.")
public class BusAlertSseController {

    private final SseEmitterService sseEmitterService;

    @Operation(summary = "SSE 연결", description = "사용자 ID를 기반으로 SSE 연결을 생성하여 버스 도착 정보를 실시간으로 수신합니다.")
    @Parameters({
            @Parameter(name = "userId", description = "사용자 ID", required = true)
    })
    @GetMapping("/stream")
    public SseEmitter stream(@RequestParam Long userId) {
        // 사용자 ID를 기반으로 SSE 연결을 생성하고 반환
        return sseEmitterService.connect(userId);
    }
}
