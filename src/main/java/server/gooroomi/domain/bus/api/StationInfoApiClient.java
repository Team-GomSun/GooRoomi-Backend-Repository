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
public class StationInfoApiClient {

    @Value("${openapi.bus.key}")
    private String serviceKey;

    /**
     * 사용자 위치(위도, 경도)와 반경을 이용해 근처 버스 정류소 목록을 조회
     */
    public String getNearbyStations(Double latitude, Double longitude, int radius) {
        try {
            String url = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos";
            StringBuilder urlBuilder = new StringBuilder(url);
            urlBuilder.append("?serviceKey=").append(serviceKey);
            urlBuilder.append("&tmX=").append(URLEncoder.encode(latitude.toString(), "UTF-8"));
            urlBuilder.append("&tmY=").append(URLEncoder.encode(longitude.toString(), "UTF-8"));
            urlBuilder.append("&radius=").append(radius);
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
            throw new RuntimeException("정류소 조회 중 오류 발생", e);
        }
    }
}
