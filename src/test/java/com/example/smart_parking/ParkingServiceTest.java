package com.example.smart_parking;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ParkingServiceTest {

    @Test
    void carFeeHasOneHourMinimum() {
        assertEquals(20.0, ParkingService.calculateFee("car", 10), 0.001);
    }

    @Test
    void bikeFeeRoundsUpPartialHours() {
        assertEquals(20.0, ParkingService.calculateFee("bike", 61), 0.001);
    }

    @Test
    void invalidVehicleTypeIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> ParkingService.calculateFee("truck", 60));
    }

    @Test
    void parkAllocatesNearestFreeSlot() {
        ParkingService s = new ParkingService();
        LocalDateTime t = LocalDateTime.of(2026, 10, 29, 10, 0);
        assertEquals(1, s.park("KA01", "car", t).get("slot"));
        assertEquals(2, s.park("KA02", "bike", t).get("slot"));
    }

    @Test
    void lotFullIsRejected() {
        ParkingService s = new ParkingService();
        LocalDateTime t = LocalDateTime.of(2026, 10, 29, 10, 0);
        for (int i = 1; i <= ParkingService.TOTAL_SLOTS; i++) s.park("V" + i, "car", t);
        assertThrows(IllegalStateException.class, () -> s.park("EXTRA", "car", t));
    }

    @Test
    void leaveChargesBasedOnParkedTime() {
        ParkingService s = new ParkingService();
        LocalDateTime in = LocalDateTime.of(2026, 10, 29, 10, 0);
        s.park("KA01", "car", in);
        Map<String, Object> r = s.leave("KA01", in.plusMinutes(90));
        assertEquals(40.0, (double) r.get("fee"), 0.001);
    }
}
