package server.gooroomi.domain.bus.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import server.gooroomi.domain.bus.converter.BusConverter;
import server.gooroomi.domain.bus.dto.OcrProcessRequest;
import server.gooroomi.domain.bus.dto.OcrProcessResponse;
import server.gooroomi.domain.bus.entity.BusArrival;
import server.gooroomi.domain.bus.entity.BusStation;
import server.gooroomi.domain.bus.entity.MatchType;
import server.gooroomi.domain.user.entity.User;
import server.gooroomi.domain.user.repository.UserRepository;
import server.gooroomi.global.handler.response.BaseException;
import server.gooroomi.global.handler.response.BaseResponse;
import server.gooroomi.global.handler.response.BaseResponseStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusOcrMatchingService {

    private final UserRepository userRepository;
    private final StringSimilarityService similarityService;
    private static final double SIMILARITY_THRESHOLD = 0.8; // 유사도 임계값

    /**
     * OCR 결과를 처리하여 버스 번호와 매칭
     */
    @Transactional
    public BaseResponse<OcrProcessResponse> processOcrResult(OcrProcessRequest request) {
        // 사용자 및 버스 정류장 조회
        BusStation busStation = getUserBusStation(request.getUserId());
        List<BusArrival> busArrivals = busStation.getBusArrivals();

        // 정확히 일치하는 버스 번호 찾기
        Optional<OcrProcessResponse> exactMatchResponse = findExactMatchResponse(busArrivals, request.getOcrText());
        if (exactMatchResponse.isPresent()) {
            return BaseResponse.success(exactMatchResponse.get());
        }

        // 유사한 버스 번호 찾기
        Optional<OcrProcessResponse> similarMatchResponse = findSimilarMatchResponse(busArrivals, request.getOcrText());
        if (similarMatchResponse.isPresent()) {
            return BaseResponse.success(similarMatchResponse.get());
        }

        /**
         * 일치하는 버스가 없는 경우 (정확히 일치하지도 않고, 유사하지도 않은 경우)
         * 다음 경우가 포함됨
         * 1. 버스 목록이 비어있는 경우
         * 2. 유사도가 임계값보다 낮은 경우
         */
        OcrProcessResponse response = BusConverter.toOCRProcessResponse(request.getOcrText(), request.getOcrText(),
                MatchType.NONE);
        return BaseResponse.success(response);
    }

    /**
     * 사용자 ID로 버스 정류장 정보 조회
     */
    private BusStation getUserBusStation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        BusStation busStation = user.getBusStation();
        if (busStation == null) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_STATION);
        }
        return busStation;
    }

    /**
     * 정확히 일치하는 버스 번호 찾기
     */
    private Optional<OcrProcessResponse> findExactMatchResponse(List<BusArrival> busArrivals, String ocrText) {
        return busArrivals.stream().filter(arrival -> arrival.getBusNumber().equals(ocrText)).findFirst()
                .map(arrival -> {
                    String busNumber = arrival.getBusNumber();
                    return BusConverter.toOCRProcessResponse(busNumber, ocrText, MatchType.EXACT);
                });
    }

    /**
     * 유사한 버스 번호 찾기 유사도가 임계값 이상인 경우에만 결과를 반환하고, 그렇지 않은 경우에는 Optional.empty()를 반환
     */
    private Optional<OcrProcessResponse> findSimilarMatchResponse(List<BusArrival> busArrivals, String ocrText) {
        Optional<BusArrival> mostSimilarBus = findMostSimilarBus(busArrivals, ocrText);

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
    private Optional<BusArrival> findMostSimilarBus(List<BusArrival> busArrivals, String ocrText) {
        return busArrivals.stream().max(Comparator.comparingDouble(
                arrival -> similarityService.calculateJaroWinklerSimilarity(arrival.getBusNumber(), ocrText)));
    }

    /**
     * 유사도 정보 로깅
     */
    private void logSimilarityInfo(String ocrText, String mostSimilarBusNumber, double similarity) {
        log.info("OCR 결과: {}, 가장 유사한 버스 번호: {}, 유사도: {}", ocrText, mostSimilarBusNumber, similarity);
    }
}
