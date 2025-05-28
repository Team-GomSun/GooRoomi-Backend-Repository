package server.gooroomi.domain.bus.entity;

public enum MatchType {
    EXACT, // 정확히 일치
    SIMILAR, // 유사도 기반 매칭 성공
    NONE // 매칭 실패
}
