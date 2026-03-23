package com.example.parking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {

        // -- wiring (composition root) --
        PricingPolicy pricing = new FlatRatePricingPolicy(Map.of(
                SlotType.SMALL,  10,
                SlotType.MEDIUM, 20,
                SlotType.LARGE,  50
        ));
        BillingService billing   = new BillingService(pricing);
        SlotAssignmentStrategy strategy = new NearestSlotStrategy();

        List<ParkingSlot> slots = List.of(
                new ParkingSlot(1, SlotType.SMALL,  0),
                new ParkingSlot(2, SlotType.SMALL,  0),
                new ParkingSlot(3, SlotType.MEDIUM, 0),
                new ParkingSlot(4, SlotType.MEDIUM, 0),
                new ParkingSlot(5, SlotType.LARGE,  0),
                new ParkingSlot(6, SlotType.SMALL,  1),
                new ParkingSlot(7, SlotType.MEDIUM, 1),
                new ParkingSlot(8, SlotType.LARGE,  1)
        );

        ParkingLot lot = new ParkingLot(slots, strategy, billing);

        Gate groundGate = new Gate(1, 0);
        Gate firstFloorGate = new Gate(2, 1);

        // ---- entries ----
        Vehicle bike = new Vehicle("KA-01-1234", VehicleType.TWO_WHEELER);
        ParkingTicket t1 = lot.entry(bike, groundGate,
                LocalDateTime.of(2026, 3, 23, 10, 0));
        System.out.println("Bike parked  -> " + t1.getTicketId()
                + ", " + t1.getSlot());

        Vehicle car = new Vehicle("KA-02-5678", VehicleType.CAR);
        ParkingTicket t2 = lot.entry(car, firstFloorGate,
                LocalDateTime.of(2026, 3, 23, 10, 15));
        System.out.println("Car parked   -> " + t2.getTicketId()
                + ", " + t2.getSlot());

        Vehicle bus = new Vehicle("KA-03-9999", VehicleType.BUS);
        ParkingTicket t3 = lot.entry(bus, groundGate,
                LocalDateTime.of(2026, 3, 23, 11, 0));
        System.out.println("Bus parked   -> " + t3.getTicketId()
                + ", " + t3.getSlot());

        // ---- exits ----
        System.out.println();
        Bill b1 = lot.exit(t1.getTicketId(),
                LocalDateTime.of(2026, 3, 23, 12, 30));
        System.out.println("Bike exited  -> " + b1);

        Bill b2 = lot.exit(t2.getTicketId(),
                LocalDateTime.of(2026, 3, 23, 14, 15));
        System.out.println("Car exited   -> " + b2);

        Bill b3 = lot.exit(t3.getTicketId(),
                LocalDateTime.of(2026, 3, 23, 12, 0));
        System.out.println("Bus exited   -> " + b3);

        // ---- overflow demo: bike spills into MEDIUM slot ----
        System.out.println();
        Vehicle bike2 = new Vehicle("KA-04-1111", VehicleType.TWO_WHEELER);
        Vehicle bike3 = new Vehicle("KA-04-2222", VehicleType.TWO_WHEELER);
        lot.entry(bike2, groundGate, LocalDateTime.of(2026, 3, 23, 13, 0));
        lot.entry(bike3, groundGate, LocalDateTime.of(2026, 3, 23, 13, 0));

        Vehicle bike4 = new Vehicle("KA-04-3333", VehicleType.TWO_WHEELER);
        ParkingTicket t4 = lot.entry(bike4, groundGate,
                LocalDateTime.of(2026, 3, 23, 13, 5));
        System.out.println("Bike overflow -> " + t4.getSlot()
                + " — charged at MEDIUM rate");
    }
}
