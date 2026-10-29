package com.example.smart_parking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class ParkingController {
    private final ParkingService service = new ParkingService();

    @PostMapping("/park")
    public Map<String, Object> park(@RequestBody Map<String, String> body) {
        return service.park(body.get("vehicle"), body.get("type"), LocalDateTime.now());
    }

    @PostMapping("/leave")
    public Map<String, Object> leave(@RequestBody Map<String, String> body) {
        return service.leave(body.get("vehicle"), LocalDateTime.now());
    }

    @GetMapping("/slots")
    public List<Map<String, Object>> slots() {
        return service.getSlots();
    }

    // Stateless: same answer on every pod
    @GetMapping("/fee")
    public Map<String, Object> fee(@RequestParam double hours, @RequestParam String type) {
        double fee = ParkingService.calculateFee(type, Math.round(hours * 60));
        return Map.of("type", type, "hours", hours, "fee", fee);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(Exception e) { return Map.of("error", e.getMessage()); }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflict(Exception e) { return Map.of("error", e.getMessage()); }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(Exception e) { return Map.of("error", e.getMessage()); }
}
