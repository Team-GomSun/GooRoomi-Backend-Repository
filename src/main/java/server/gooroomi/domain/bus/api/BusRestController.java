package server.gooroomi.domain.bus.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.gooroomi.domain.bus.application.BusService;
import server.gooroomi.domain.bus.dto.BusArrivalResponse;
import server.gooroomi.global.handler.response.BaseResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bus")
public class BusRestController {

    private final BusService busService;

    @GetMapping("/arrival")
    public BaseResponse<List<BusArrivalResponse>> getArrivingBuses(Long userId){
        return busService.getArrivingBuses(userId);
    }
}
