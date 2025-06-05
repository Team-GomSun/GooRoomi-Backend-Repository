package server.gooroomi.domain.bus.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import server.gooroomi.domain.bus.dto.BusArrivalDto;
import server.gooroomi.domain.user.application.UserService;
import server.gooroomi.domain.user.entity.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 버스 알림 서비스 사용자에게 버스 도착 알림 전송
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusAlertService {

    private static final Long TIMEOUT = 60 * 1000L * 10; // 10분 유지
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final UserService userService;

    /**
     * 사용자를 위한 SSE 연결 생성
     */
    public SseEmitter createEmitter(Long userId) {
        // 기존 Emitter가 있으면 제거
        removeEmitterIfExists(userId);

        // 새 Emitter 생성
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitters.put(userId, emitter);
        log.info("[SSE Emitter 생성] userId={}", userId);

        // 완료 시 Emitter 제거
        emitter.onCompletion(() -> {
            emitters.remove(userId);
            log.info("[SSE 연결 종료] userId={}", userId);
        });

        // 타임아웃 시 Emitter 제거
        emitter.onTimeout(() -> {
            emitters.remove(userId);
            log.warn("[SSE 타임아웃] userId={}", userId);
        });

        // 오류 발생 시 Emitter 제거
        emitter.onError((e) -> {
            emitters.remove(userId);
            log.error("[SSE 오류] userId={}, error={}", userId, e.getMessage(), e);
        });

        // 초기 연결 이벤트 전송
        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE 연결 완료"));
            log.info("[초기 이벤트 전송 완료] userId={}", userId);
        } catch (IOException e) {
            emitters.remove(userId);
            log.error("[초기 이벤트 전송 실패] userId={}, error={}", userId, e.getMessage(), e);
        }

        return emitter;
    }

    /**
     * 기존 Emitter가 있으면 제거
     */
    private void removeEmitterIfExists(Long userId) {
        SseEmitter existingEmitter = emitters.get(userId);
        if (existingEmitter != null) {
            try {
                existingEmitter.complete();
            } catch (Exception e) {
                log.warn("[기존 Emitter 제거 중 오류] userId={}, error={}", userId, e.getMessage());
            } finally {
                emitters.remove(userId);
                log.info("[기존 Emitter 제거] userId={}", userId);
            }
        }
    }

    /**
     * 사용자에게 버스 도착 알림 전송 도착 예정 버스 목록 중 사용자가 등록한 버스가 있는지 확인하고 알림 전송
     */
    public void notifyUserIfBusArriving(Long userId, List<BusArrivalDto> busArrivals) {
        // Emitter가 없으면 조용히 반환
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            log.warn("[알림 전송 스킵] userId={} - Emitter 없음", userId);
            return;
        }

        // 사용자 정보 조회
        User user = userService.getUserById(userId);
        String userBusNumber = user.getBusNumber();

        // 사용자가 버스 번호를 등록하지 않은 경우
        if (userBusNumber == null || userBusNumber.isEmpty()) {
            log.warn("[알림 전송 스킵] userId={} - 등록된 버스 번호 없음", userId);
            return;
        }

        // 사용자가 등록한 버스가 도착 예정인지 확인
        boolean found = busArrivals.stream().anyMatch(arrival -> arrival.getBusNumber().equals(userBusNumber));

        // 사용자가 등록한 버스가 도착 예정이면 알림 전송
        if (found) {
            try {
                // 알림 전송
                emitter.send(SseEmitter.event()
                        .name("bus-arrival")
                        .data(userBusNumber + "번 버스가 곧 도착합니다."));
                log.info("[알림 전송 성공] userId={}, bus={}", userId, userBusNumber);
            } catch (IOException e) {
                emitters.remove(userId);
                log.error("[알림 전송 실패] userId={}, error={}", userId, e.getMessage(), e);
            }
        }
    }

    /**
     * 모든 활성 SSE 연결 수 반환
     */
    public int getActiveConnectionCount() {
        return emitters.size();
    }
}