# Parking Lot — LLD (SOLID + Strategy Pattern)

## Narrative
A multi-floor parking lot needs to manage vehicle entry, slot assignment, and
billing at exit. Different vehicle types (two-wheelers, cars, buses) need
different slot sizes, and the system should be extensible for new pricing
policies or slot-selection algorithms without modifying core logic.

## Design Highlights

| SOLID Principle | Where it shows up |
|---|---|
| **SRP** | `ParkingLot` orchestrates; `BillingService` handles billing; `VehicleSlotMapping` owns compatibility rules |
| **OCP** | New slot strategies implement `SlotAssignmentStrategy`; new pricing via `PricingPolicy` — no existing code changes |
| **LSP** | Any `PricingPolicy` impl can replace `FlatRatePricingPolicy` without breaking `BillingService` |
| **ISP** | Interfaces are small and focused (`PricingPolicy` has one method, `SlotAssignmentStrategy` has one method) |
| **DIP** | `ParkingLot` depends on abstractions (`SlotAssignmentStrategy`, `BillingService`) not concrete classes |

## Design Patterns Used
- **Strategy** — `SlotAssignmentStrategy` / `NearestSlotStrategy` for pluggable slot selection
- **Composition Root** — all wiring happens in `App.main()`; no hidden `new` inside domain classes

## Build & Run
```bash
cd parking-lot/src
javac com/example/parking/*.java
java com.example.parking.App
```
