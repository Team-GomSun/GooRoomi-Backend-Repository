package server.gooroomi.domain.bus.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.gooroomi.domain.bus.application.BusService;
import server.gooroomi.domain.bus.dto.BusArrivalResponse;
import server.gooroomi.global.handler.response.BaseResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bus")
@Tag(name = "Bus", description = "Bus 관련 API")
public class BusRestController {

        private final BusService busService;

        @Operation(summary = "곧 도착 버스 목록 조회", description = """
                        사용자가 등록한 버스가 목록에 있는 경우, "code" : 20002 와 함께 응답을 반환.
                        """)
        @GetMapping("/arrivals")
        public BaseResponse<List<BusArrivalResponse>> getBusArrivals(@RequestParam Long userId) {
                return busService.getBusArrivals(userId);
        }
}
