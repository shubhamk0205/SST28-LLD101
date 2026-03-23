package com.example.parking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Core orchestrator: handles vehicle entry, slot assignment via strategy,
 * and exit with billing. Keeps track of active tickets.
 *
 * Follows SRP — it only coordinates; slot finding is delegated to a
 * SlotAssignmentStrategy and billing to BillingService.
 */
public class ParkingLot {
    private final List<ParkingSlot> slots;
    private final SlotAssignmentStrategy assignmentStrategy;
    private final BillingService billingService;
    private final Map<String, ParkingTicket> activeTickets;
    private int ticketCounter;

    public ParkingLot(List<ParkingSlot> slots,
                      SlotAssignmentStrategy assignmentStrategy,
                      BillingService billingService) {
        this.slots              = new ArrayList<>(Objects.requireNonNull(slots));
        this.assignmentStrategy = Objects.requireNonNull(assignmentStrategy);
        this.billingService     = Objects.requireNonNull(billingService);
        this.activeTickets      = new HashMap<>();
        this.ticketCounter      = 0;
    }

    public ParkingTicket entry(Vehicle vehicle, Gate entryGate, LocalDateTime entryTime) {
        Optional<ParkingSlot> found = assignmentStrategy.findSlot(slots, vehicle.getType(), entryGate);
        if (found.isEmpty()) {
            throw new IllegalStateException("No available slot for " + vehicle);
        }

        ParkingSlot slot = found.get();
        slot.occupy();

        String id = "TKT-" + (++ticketCounter);
        ParkingTicket ticket = new ParkingTicket(id, vehicle, slot, entryTime);
        activeTickets.put(id, ticket);
        return ticket;
    }

    public Bill exit(String ticketId, LocalDateTime exitTime) {
        ParkingTicket ticket = activeTickets.remove(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("No active ticket found: " + ticketId);
        }
        ticket.getSlot().free();
        return billingService.generateBill(ticket, exitTime);
    }
}
