package server.gooroomi.domain.bus.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.gooroomi.domain.bus.entity.MatchType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OcrProcessResponse {
    private String proccessedBusNumber; // 가공된 버스 번호(가공 후)
    private String rawOcrText; // OCR로 추출된 텍스트(가공 전)
    private MatchType matchType; //OCR 텍스트와 버스 번호 간의 매칭 결과 유형

    @Builder
    public OcrProcessResponse(String proccessedBusNumber, String rawOcrText, MatchType matchType) {
        this.proccessedBusNumber = proccessedBusNumber;
        this.rawOcrText = rawOcrText;
        this.matchType = matchType;
    }
}