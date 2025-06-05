package server.gooroomi.domain.bus.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import server.gooroomi.domain.bus.converter.BusConverter;
import server.gooroomi.domain.bus.dto.BusArrivalDto;
import server.gooroomi.domain.bus.dto.BusStationDto;
import server.gooroomi.domain.bus.dto.OcrProcessRequest;
import server.gooroomi.domain.bus.dto.OcrProcessResponse;
import server.gooroomi.domain.bus.MatchType;
import server.gooroomi.domain.user.application.UserService;
import server.gooroomi.domain.user.entity.User;
import server.gooroomi.global.handler.response.BaseException;
import server.gooroomi.global.handler.response.BaseResponse;
import server.gooroomi.global.handler.response.BaseResponseStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * OCR 버스 번호 매칭 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusOcrMatchingService {

    private final UserService userService;
    private final StringSimilarityService similarityService;
    private final BusStationService busStationService;
    private final BusArrivalService busArrivalService;
    private static final double SIMILARITY_THRESHOLD = 0.8; // 유사도 임계값

    /**
     * OCR 결과를 처리하여 버스 번호와 매칭
     */
    @Transactional
    public BaseResponse<OcrProcessResponse> processOcrResult(OcrProcessRequest request) {
        log.info("[OCR 처리 요청] userId: {}, ocrText: {}", request.getUserId(), request.getOcrText());

        // 사용자 조회
        User user = userService.getUserById(request.getUserId());

        // 위치 정보 확인
        if (user.getLatitude() == null || user.getLongitude() == null) {
            throw new BaseException(BaseResponseStatus.LOCATION_NOT_REGISTERED);
        }

        // 가장 가까운 정류장 조회
        BusStationDto stationDto = busStationService.findNearestStation(user.getLatitude(), user.getLongitude());

        // 도착 예정 버스 목록 조회
        List<BusArrivalDto> busArrivals = busArrivalService.getBusArrivals(stationDto.getArsId());

        // 정확히 일치하는 버스 번호 찾기
        Optional<OcrProcessResponse> exactMatchResponse = findExactMatchResponse(busArrivals, request.getOcrText());
        if (exactMatchResponse.isPresent()) {
            log.info("정확히 일치하는 버스 번호 찾음 - userId: {}, ocrText: {}, busNumber: {}", user.getId(), request.getOcrText(),
                    exactMatchResponse.get().getProccessedBusNumber());
            return BaseResponse.success(exactMatchResponse.get());
        }

        // 유사한 버스 번호 찾기
        Optional<OcrProcessResponse> similarMatchResponse = findSimilarMatchResponse(busArrivals, request.getOcrText());
        if (similarMatchResponse.isPresent()) {
            return BaseResponse.success(similarMatchResponse.get());
        }

        /*
         * 일치하는 버스가 없는 경우 (정확히 일치하지도 않고, 유사하지도 않은 경우) 다음 경우가 포함됨
         * 1. 버스 목록이 비어있는 경우
         * 2. 유사도가 임계값보다 낮은 경우
         */
        OcrProcessResponse response = BusConverter.toOCRProcessResponse(request.getOcrText(), request.getOcrText(),
                MatchType.NONE);

        return BaseResponse.success(response);
    }

    /**
     * 정확히 일치하는 버스 번호 찾기
     */
    private Optional<OcrProcessResponse> findExactMatchResponse(List<BusArrivalDto> busArrivals, String ocrText) {
        return busArrivals.stream().filter(arrival -> arrival.getBusNumber().equals(ocrText)).findFirst()
                .map(arrival -> {
                    String busNumber = arrival.getBusNumber();
                    return BusConverter.toOCRProcessResponse(busNumber, ocrText, MatchType.EXACT);
                });
    }

    /**
     * 유사한 버스 번호 찾기 유사도가 임계값 이상인 경우에만 결과를 반환하고, 그렇지 않은 경우에는 Optional.empty()를 반환
     */
    private Optional<OcrProcessResponse> findSimilarMatchResponse(List<BusArrivalDto> busArrivals, String ocrText) {
        Optional<BusArrivalDto> mostSimilarBus = findMostSimilarBus(busArrivals, ocrText);

        if (mostSimilarBus.isPresent()) {
            String mostSimilarBusNumber = mostSimilarBus.get().getBusNumber();
            double similarity = similarityService.calculateJaroWinklerSimilarity(mostSimilarBusNumber, ocrText);

            logSimilarityInfo(ocrText, mostSimilarBusNumber, similarity);

            // 유사도가 임계값 이상인 경우에만 유사한 버스 번호로 응답 생성
            if (similarity >= SIMILARITY_THRESHOLD) {
                return Optional.of(BusConverter.toOCRProcessResponse(mostSimilarBusNumber, ocrText, MatchType.SIMILAR));
            }
        }
        // 유사도가 임계값 미만이거나 버스가 없는 경우
        return Optional.empty();
    }

    /**
     * 가장 유사한 버스 찾기
     */
    private Optional<BusArrivalDto> findMostSimilarBus(List<BusArrivalDto> busArrivals, String ocrText) {
        return busArrivals.stream().max(Comparator.comparingDouble(
                arrival -> similarityService.calculateJaroWinklerSimilarity(arrival.getBusNumber(), ocrText)));
    }

    /**
     * 유사도 정보 로깅
     */
    private void logSimilarityInfo(String ocrText, String mostSimilarBusNumber, double similarity) {
        log.info("[유사도 매칭 결과] ocrText: {}, 가장 유사한 버스 번호: {}, 유사도: {}", ocrText, mostSimilarBusNumber, similarity);
    }
}
