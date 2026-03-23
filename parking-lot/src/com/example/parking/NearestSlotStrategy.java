package com.example.parking;

import java.util.List;
import java.util.Optional;

/**
 * Picks the closest available slot relative to the entry gate's floor,
 * breaking ties by lower slot number. This keeps the vehicles near the
 * gate they entered from, reducing walking distance.
 */
public class NearestSlotStrategy implements SlotAssignmentStrategy {

    @Override
    public Optional<ParkingSlot> findSlot(List<ParkingSlot> slots, VehicleType vehicleType, Gate entryGate) {
        List<SlotType> allowed = VehicleSlotMapping.compatibleSlots(vehicleType);

        return slots.stream()
                .filter(s -> !s.isOccupied())
                .filter(s -> allowed.contains(s.getType()))
                .min((a, b) -> {
                    int distA = Math.abs(a.getFloor() - entryGate.getFloor()) * 1000 + a.getSlotNumber();
                    int distB = Math.abs(b.getFloor() - entryGate.getFloor()) * 1000 + b.getSlotNumber();
                    return Integer.compare(distA, distB);
                });
    }
}
