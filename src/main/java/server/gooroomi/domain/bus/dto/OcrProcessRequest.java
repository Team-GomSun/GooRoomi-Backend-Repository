package server.gooroomi.domain.bus.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OcrProcessRequest {
    private String ocrText; // OCR로 추출된 텍스트
    private Long userId; // process.ts에서 자동으로 추가됨
}
