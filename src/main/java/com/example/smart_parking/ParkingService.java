package com.example.smart_parking;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ParkingService {
    public static final int TOTAL_SLOTS = 10;

    private final Map<Integer, String> slots = new TreeMap<>();
    private final Map<String, LocalDateTime> entryTimes = new HashMap<>();
    private final Map<String, String> types = new HashMap<>();

    // Fee: car 20/hour, bike 10/hour, minimum 1 hour, partial hours round up
    public static double calculateFee(String type, long minutes) {
        double rate;
        if ("car".equalsIgnoreCase(type)) rate = 20;
        else if ("bike".equalsIgnoreCase(type)) rate = 10;
        else throw new IllegalArgumentException("type must be car or bike");
        long hours = Math.max(1, (long) Math.ceil(minutes / 60.0));
        return hours * rate;
    }

    public Map<String, Object> park(String vehicle, String type, LocalDateTime entry) {
        calculateFee(type, 0);
        if (entryTimes.containsKey(vehicle))
            throw new IllegalStateException("Vehicle already parked");
        for (int i = 1; i <= TOTAL_SLOTS; i++) {
            if (!slots.containsKey(i)) {
                slots.put(i, vehicle);
                entryTimes.put(vehicle, entry);
                types.put(vehicle, type.toLowerCase());
                Map<String, Object> r = new LinkedHashMap<>();
                r.put("vehicle", vehicle);
                r.put("slot", i);
                r.put("entry", entry.toString());
                return r;
            }
        }
        throw new IllegalStateException("Lot is full");
    }

    public Map<String, Object> leave(String vehicle, LocalDateTime exit) {
        LocalDateTime entry = entryTimes.get(vehicle);
        if (entry == null) throw new NoSuchElementException("Vehicle not found");
        long minutes = Duration.between(entry, exit).toMinutes();
        double fee = calculateFee(types.get(vehicle), minutes);
        int slot = 0;
        for (Map.Entry<Integer, String> e : slots.entrySet())
            if (e.getValue().equals(vehicle)) slot = e.getKey();
        slots.remove(slot);
        entryTimes.remove(vehicle);
        types.remove(vehicle);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("vehicle", vehicle);
        r.put("slot", slot);
        r.put("minutes", minutes);
        r.put("fee", fee);
        return r;
    }

    public List<Map<String, Object>> getSlots() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i <= TOTAL_SLOTS; i++) {
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("slot", i);
            s.put("vehicle", slots.get(i));
            s.put("free", !slots.containsKey(i));
            list.add(s);
        }
        return list;
    }
}
