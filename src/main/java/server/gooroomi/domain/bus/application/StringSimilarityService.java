package server.gooroomi.domain.bus.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 문자열 유사도 계산을 위한 Service 클래스
 * Jaro-Winkler 알고리즘을 사용하여 문자열 간의 유사도를 계산
 */
@Service
@Slf4j
public class StringSimilarityService {

    private static final double JARO_WINKLER_PREFIX_WEIGHT = 0.1; // Jaro-Winkler 접두사 가중치

    /**
     * Jaro-Winkler 유사도 계산 알고리즘 구현 문자열 간의 유사성을 측정하며, 특히 시작 부분이 일치할 때 더 높은 점수를 부여함
     * 
     * @param s1 첫 번째 문자열
     * @param s2 두 번째 문자열
     * @return 두 문자열 간의 Jaro-Winkler 유사도 (0.0 ~ 1.0)
     */
    public double calculateJaroWinklerSimilarity(String s1, String s2) {
        // 두 문자열이 같으면 유사도는 1.0
        if (s1.equals(s2)) {
            return 1.0;
        }

        // 두 문자열 중 하나라도 비어있으면 유사도는 0.0
        if (s1 == null || s2 == null || s1.isEmpty() || s2.isEmpty()) {
            return 0.0;
        }

        // Jaro 유사도 계산
        double jaroSimilarity = calculateJaroSimilarity(s1, s2);

        // 공통 접두사 길이 계산 (최대 4자까지)
        int prefixLength = 0;
        int maxPrefixLength = Math.min(4, Math.min(s1.length(), s2.length()));

        for (int i = 0; i < maxPrefixLength; i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                prefixLength++;
            } else {
                break;
            }
        }

        // Jaro-Winkler 유사도 계산: Jaro 유사도 + (접두사 길이 * 가중치 * (1 - Jaro 유사도))
        return jaroSimilarity + (prefixLength * JARO_WINKLER_PREFIX_WEIGHT * (1.0 - jaroSimilarity));
    }

    /**
     * Jaro 유사도 계산 알고리즘 구현
     * 
     * @param s1 첫 번째 문자열
     * @param s2 두 번째 문자열
     * @return 두 문자열 간의 Jaro 유사도 (0.0 ~ 1.0)
     */
    private double calculateJaroSimilarity(String s1, String s2) {
        // 두 문자열의 길이
        int len1 = s1.length();
        int len2 = s2.length();

        // 최대 일치 거리 계산 (두 문자열 길이 중 큰 값 / 2 - 1)
        int maxDistance = Math.max(0, Math.max(len1, len2) / 2 - 1);

        // 일치하는 문자 찾기
        boolean[] matched1 = new boolean[len1];
        boolean[] matched2 = new boolean[len2];

        int matchCount = 0; // 일치하는 문자 수

        for (int i = 0; i < len1; i++) {
            int start = Math.max(0, i - maxDistance);
            int end = Math.min(len2 - 1, i + maxDistance);

            for (int j = start; j <= end; j++) {
                if (!matched2[j] && s1.charAt(i) == s2.charAt(j)) {
                    matched1[i] = true;
                    matched2[j] = true;
                    matchCount++;
                    break;
                }
            }
        }

        // 일치하는 문자가 없으면 유사도는 0.0
        if (matchCount == 0) {
            return 0.0;
        }

        // 전환된 문자 수 계산
        int transpositions = 0;
        int k = 0;

        for (int i = 0; i < len1; i++) {
            if (matched1[i]) {
                while (!matched2[k]) {
                    k++;
                }

                if (s1.charAt(i) != s2.charAt(k)) {
                    transpositions++;
                }

                k++;
            }
        }

        // 전환은 쌍으로 계산하므로 2로 나눔
        transpositions /= 2;

        // Jaro 유사도 계산: (일치 문자 비율 + 일치 문자 비율 + (일치 문자 - 전환) / 일치 문자) / 3
        double m = matchCount;
        return (m / len1 + m / len2 + (m - transpositions) / m) / 3.0;
    }
}