package server.gooroomi.domain.bus.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.gooroomi.domain.bus.application.BusOcrMatchingService;
import server.gooroomi.domain.bus.application.BusService;
import server.gooroomi.domain.bus.dto.BusArrivalResponse;
import server.gooroomi.domain.bus.dto.OcrProcessRequest;
import server.gooroomi.domain.bus.dto.OcrProcessResponse;
import server.gooroomi.global.handler.response.BaseResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bus")
@Tag(name = "Bus", description = "버스 도착 정보 관련 API")
public class BusRestController {

        private final BusService busService;
        private final BusOcrMatchingService busOcrMatchingService;

        @Operation(summary = "곧 도착 버스 목록 조회", description = "사용자의 위치 기준으로 곧 도착 예정인 버스 목록을 조회합니다.")
        @Parameters({@Parameter(name = "userId", description = "사용자 ID", required = true)})
        @GetMapping("/arrivals")
        public BaseResponse<List<BusArrivalResponse>> getBusArrivals(@RequestParam Long userId) {
                return busService.getBusArrivals(userId);
        }

        @Operation(summary = "OCR 결과와 버스 번호 매칭", description = "OCR로 인식된 버스 번호와 도착 예정 버스 목록을 비교합니다.")
        @PostMapping("/ocr-process")
        public BaseResponse<OcrProcessResponse> processOcrResult(@RequestBody OcrProcessRequest request) {
                return busOcrMatchingService.processOcrResult(request);
        }
}
