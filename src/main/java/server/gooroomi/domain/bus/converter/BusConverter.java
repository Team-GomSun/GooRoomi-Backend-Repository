package server.gooroomi.domain.bus.converter;

import server.gooroomi.domain.bus.dto.OcrProcessResponse;
import server.gooroomi.domain.bus.MatchType;

public class BusConverter {
    public static OcrProcessResponse toOCRProcessResponse(String proccessedBusNumber, String rawOcrText, MatchType type) {
        return OcrProcessResponse.builder()
                .proccessedBusNumber(proccessedBusNumber)
                .rawOcrText(rawOcrText)
                .matchType(type)
                .build();
    }
}
