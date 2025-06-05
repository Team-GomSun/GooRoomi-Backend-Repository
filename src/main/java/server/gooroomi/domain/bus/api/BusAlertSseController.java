package server.gooroomi.domain.bus.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import server.gooroomi.domain.bus.application.BusAlertService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
@Tag(name = "SSE", description = "SSE를 통해 사용자에게 원하는 버스 도착 정보를 실시간으로 제공합니다.")
@Slf4j
public class BusAlertSseController {

    private final BusAlertService busAlertService;

    @Operation(summary = "SSE 연결", description = "사용자 ID를 기반으로 SSE 연결을 생성하여 버스 도착 정보를 실시간으로 수신합니다.")
    @Parameters({ @Parameter(name = "userId", description = "사용자 ID", required = true) })
    @GetMapping(value = "/stream")
    public SseEmitter stream(@RequestParam Long userId) {
        log.info("[SSE 연결 요청] userId={}", userId);

        try {
            // 기존 연결이 있으면 자동으로 정리되고 새 연결이 생성됨
            SseEmitter emitter = busAlertService.createEmitter(userId);
            log.info("[SSE 연결 성공] userId={}, 현재 활성 연결 수={}", userId, busAlertService.getActiveConnectionCount());
            return emitter;
        } catch (Exception e) {
            log.error("[SSE 연결 실패] userId={}, error={}", userId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "SSE 연결 실패: " + e.getMessage(), e);
        }
    }
}
