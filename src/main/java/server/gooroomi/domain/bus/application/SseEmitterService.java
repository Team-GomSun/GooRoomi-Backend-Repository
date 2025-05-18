package server.gooroomi.domain.bus.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import server.gooroomi.domain.bus.entity.BusArrival;
import server.gooroomi.domain.user.entity.User;
import server.gooroomi.domain.user.repository.UserRepository;
import server.gooroomi.global.handler.response.BaseException;
import server.gooroomi.global.handler.response.BaseResponseStatus;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private static final Long TIMEOUT = 60 * 1000L;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final UserRepository userRepository;

    // 사용자가 SSE 연결을 맺고 이벤트 발생 시 알림을 보낼 수 있도록 설정
    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT); // 타임아웃 설정
        emitters.put(userId, emitter); // 사용자 ID를 key로 emitter 저장

        // 연결 종료 시 emitter 제거
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        return emitter;
    }

    // 도착 예정 버스 목록 중 사용자가 원하는 버스가 있으면 SSE 이벤트 전송
    public void notifyUserIfBusArriving(Long userId, List<BusArrival> busArrivals) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        boolean found = busArrivals.stream()
                .anyMatch(arrival -> arrival.getBusNumber().equals(user.getBusNumber()));

        if (found) {
            try {
                emitter.send(SseEmitter.event()
                        .name("bus-arrival") // 이벤트 이름
                        .data(user.getBusNumber() + "번 버스가 곧 도착합니다.")); // 이벤트 ㅐ용
            } catch (IOException e) {
                emitters.remove(userId); // 예외 발생 시 emitter 제거
            }
        }
    }
}
