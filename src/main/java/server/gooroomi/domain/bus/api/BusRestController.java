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
import server.gooroomi.domain.bus.application.BusService;
import server.gooroomi.domain.bus.dto.BusArrivalResponse;
import server.gooroomi.global.handler.response.BaseResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bus")
@Tag(name = "Bus", description = "버스 도착 정보 관련 API")
public class BusRestController {

        private final BusService busService;

        @Operation(
                summary = "곧 도착 버스 목록 조회",
                description = "사용자의 위치 기준으로 도착 예정인 버스 목록을 조회합니다. " +
                              "사용자가 등록한 버스가 포함된 경우 code: 20002로 응답합니다."
        )
        @Parameters({
                @Parameter(name = "userId", description = "사용자 ID", required = true)
        })
        @GetMapping("/arrivals")
        public BaseResponse<List<BusArrivalResponse>> getBusArrivals(@RequestParam Long userId) {
                return busService.getBusArrivals(userId);
        }
}
