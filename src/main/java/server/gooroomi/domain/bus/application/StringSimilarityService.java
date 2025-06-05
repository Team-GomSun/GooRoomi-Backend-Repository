package server.gooroomi.domain.bus.application;

import org.springframework.stereotype.Service;

/**
 * 문자열 유사도 계산 서비스
 */
@Service
public class StringSimilarityService {

    /**
     * Jaro-Winkler 유사도 계산 0.0 (완전히 다름) ~ 1.0 (완전히 같음)
     */
    public double calculateJaroWinklerSimilarity(String s1, String s2) {
        // 빈 문자열 처리
        if (s1 == null || s2 == null) {
            return 0.0;
        }
        if (s1.isEmpty() || s2.isEmpty()) {
            return 0.0;
        }

        // 같은 문자열인 경우
        if (s1.equals(s2)) {
            return 1.0;
        }

        // Jaro 유사도 계산
        int len1 = s1.length();
        int len2 = s2.length();
        int maxDist = Math.max(len1, len2) / 2 - 1;
        maxDist = Math.max(0, maxDist); // 최소 0

        boolean[] match1 = new boolean[len1];
        boolean[] match2 = new boolean[len2];

        // 일치하는 문자 찾기
        int matches = 0;
        for (int i = 0; i < len1; i++) {
            int start = Math.max(0, i - maxDist);
            int end = Math.min(i + maxDist + 1, len2);

            for (int j = start; j < end; j++) {
                if (!match2[j] && s1.charAt(i) == s2.charAt(j)) {
                    match1[i] = true;
                    match2[j] = true;
                    matches++;
                    break;
                }
            }
        }

        // 일치하는 문자가 없는 경우
        if (matches == 0) {
            return 0.0;
        }

        // 전치된 문자 수 계산
        int transpositions = 0;
        int k = 0;
        for (int i = 0; i < len1; i++) {
            if (match1[i]) {
                while (!match2[k]) {
                    k++;
                }
                if (s1.charAt(i) != s2.charAt(k)) {
                    transpositions++;
                }
                k++;
            }
        }

        // Jaro 유사도 계산
        double jaro = ((double) matches / len1 + (double) matches / len2
                + (double) (matches - (transpositions / 2)) / matches) / 3.0;

        // Jaro-Winkler 유사도 계산 (공통 접두사에 가중치 부여)
        int prefixLength = 0;
        for (int i = 0; i < Math.min(4, Math.min(len1, len2)); i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                prefixLength++;
            } else {
                break;
            }
        }

        // Winkler 수정 (공통 접두사에 가중치 부여)
        double p = 0.1; // 가중치 계수 (일반적으로 0.1 사용)
        double result = jaro + prefixLength * p * (1 - jaro);

        return result;
    }
}