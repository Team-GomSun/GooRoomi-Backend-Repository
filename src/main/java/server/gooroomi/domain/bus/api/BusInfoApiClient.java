package server.gooroomi.domain.bus.api;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Component
@RequiredArgsConstructor
public class BusInfoApiClient {

    @Value("${openapi.bus.key}")
    private String serviceKey;

    /**
     * 정류소 번호(arsId)를 이용해 도착 예정 버스 목록을 조회
     */
    public String getBusArrivals(String arsId) {
        try {
            String url = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid";
            StringBuilder urlBuilder = new StringBuilder(url);
            urlBuilder.append("?serviceKey=").append(serviceKey);
            urlBuilder.append("&arsId=").append(URLEncoder.encode(arsId, "UTF-8"));
            urlBuilder.append("&resultType=json");

            HttpURLConnection conn = (HttpURLConnection) new URL(urlBuilder.toString()).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300 ?
                            conn.getInputStream() : conn.getErrorStream()
            ));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            conn.disconnect();

            return response.toString();
        } catch (Exception e) {
            throw new RuntimeException("버스 도착 정보 조회 중 오류 발생", e);
        }
    }
}
