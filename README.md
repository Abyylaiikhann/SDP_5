# Assignment 5 — Smart Delivery & Route Optimization Engine

Java 17+ implementation of the **Strategy Pattern** for choosing delivery routes. Contains eight candidate routes, five algorithms, runtime switching, extensible automatic selection, hard constraints, comparison mode, explanations and JUnit 5 tests.

## How to run

Open the folder as a **Maven project** in IntelliJ IDEA. Use JDK 17+.

```bash
mvn test
mvn exec:java
```

Alternatively, compile/run production code without Maven using `javac -d out src/main/java/com/example/delivery/*.java` and `java -cp out com.example.delivery.Main` (Java 17+). Test execution requires Maven/JUnit 5 dependencies.

## Architecture and the 10 report answers

1. **Problem solved:** Selection logic varies by delivery objective. Strategy isolates interchangeable route-ranking algorithms without adding a growing conditional in the planning context.
2. **Context:** `RoutePlanner`; it owns a `RouteStrategy` reference and delegates `plan()` to `strategy.decide()`.
3. **Strategy interface:** `RouteStrategy` exposes `decide(routes, request)`, `selectRoute(...)` and `getStrategyName()`.
4. **Concrete strategies:** `FastestRouteStrategy`, `CheapestRouteStrategy`, `EcoFriendlyRouteStrategy`, `BalancedRouteStrategy`, `DistanceFirstRouteStrategy` (fifth addition).
5. **Runtime switching:** `Main` calls `planner.setStrategy(next)` on the **same** planner four times; `DeliveryEngineTest.runtimeSwitchesOnSamePlanner` verifies three switches.
6. **Why Strategy rather than if/else:** Every algorithm is independently testable and substitutable; the Context does not reference any concrete strategy or use switches on type.
7. **Adding a strategy:** Implement `RouteStrategy` (preferably extend `AbstractRouteStrategy`); register it in the caller/selector if automatic selection is desired. Neither `RoutePlanner` nor existing algorithms needs modification.
8. **Strategy vs Bridge:** Strategy swaps behavior/algorithm according to a request. Bridge independently varies an abstraction and its implementation across two hierarchies (e.g., delivery type and transport backend). This implementation focuses on interchangeable scoring behavior rather than two orthogonal class hierarchies.
9. **SOLID:** SRP separates planning, business-rule filtering, scoring, and mapping. OCP permits adding a new algorithm without changing the context. DIP makes `RoutePlanner` depend on `RouteStrategy`.
10. **Current limitations:** Example uses in-memory candidate routes and deterministic heuristic scores, not live GPS/traffic APIs or optimization over a road graph. `cost` serves as base/toll charge; currency is illustrative. Comparisons use route-local normalized values and are relative to the feasible candidate pool.

## Models

- `Location`: name and WGS84 coordinates.
- `Route`: eight sample paths; distance, time, base price, CO2, traffic multiplier, weather delay, availability, restricted flag.
- `DeliveryRequest`: identifier, priority, maximum budget, maximum time, eco preference and emissions cap.
- `RouteDecision`: winning route, score, per-strategy explanation, and reasons for rejecting infeasible candidates.

## Formulas

**Hard constraints** are applied before scoring: `available == true`, `restricted == false`, `totalCost <= maxBudget`, `adjustedMinutes <= maxDeliveryMinutes` and `carbonEmission <= maxEmission`. Boundary values are accepted. If no route passes, `NoSuitableRouteException` is raised. Invalid or missing domain fields are rejected when constructed.

- Adjusted time: `estimatedMinutes * trafficMultiplier + weatherPenaltyMinutes`; fastest minimizes this, not raw travel time.
- Total cost: `baseCost + 12 * distanceKm + (priority == URGENT ? 500 : 0)`. `baseCost` represents base delivery charge and additional fixed fees (e.g., tolls). Cheapest minimizes total cost.
- Eco score: `0.8 * carbonEmission + 0.2 * distanceKm`. Eco-friendly minimizes environmental impacts, using emissions as the primary objective and distance as a secondary proxy for resource use.
- Balanced score: `0.40*(adjustedTime/maxFeasibleTime) + 0.30*(totalCost/maxFeasibleCost) + 0.10*(distance/maxFeasibleDistance) + 0.20*(CO2/maxFeasibleCO2)` (default configuration). Every maximum is calculated over **feasible** routes; denominator has a positive numerical floor for zero-only datasets. Smaller scores are better. The four weights are configurable, finite, within [0,1], and must sum to 1 within `1e-9`.
- Distance-first (new fifth strategy): minimizes feasible `distanceKm`.

The different scoring calculations belong in the Concrete Strategies; the universal hard business restrictions are centralized in `RouteRules` to avoid inconsistent feasibility checks.

## Automatic selection

`StrategySelector` uses a registration map rather than a large `switch`: `URGENT -> Fastest`, `ECONOMY -> Cheapest`, `ECO -> Eco-friendly`, `STANDARD -> Balanced`. `ecoPreferred=true` on a `STANDARD` request changes its priority mapping to Eco, but never overrides explicit Urgent/Economy. StrategySelector decides **which algorithm**; the selected Strategy decides **which route**.

## Example console output (excerpt)

```text
DELIVERY REQUEST #105
Automatic selection: Balanced
Fastest -> R2 | time 23.1 min
Cheapest -> R5 | time 51.5 min
Eco-friendly -> R6 | time 33.0 min
Distance-first -> R6 | time 33.0 min
```

For each decision the actual program also prints formula details and the rejection reasons. `--- STRATEGY COMPARISON ---` runs all five algorithms on the **same request** and prints each result; the later section uses different requests for automatic policy selection.

## Tests

`src/test/java/com/example/delivery/DeliveryEngineTest.java` contains **26 JUnit 5 tests** spanning all five algorithms, switching, priority mapping, cost/deadline/emissions constraints, invalid weights, boundary values, empty candidates, explanations, comparison mode and malformed inputs. Run `mvn test` to get authoritative JUnit results. Production sources have separately been compiled/run with Java 21 (`--release 17`).

## UML

- [Class diagram](docs/class-diagram.puml) — `RoutePlanner o--> RouteStrategy` and all concrete strategies.
- [Sequence diagram](docs/sequence-diagram.puml) — urgent request from client via selector, planner, filtering, scoring and decision.

Both are standard PlantUML sources; paste them in any PlantUML viewer or IntelliJ PlantUML plugin to render. They correspond to actual classes and methods.

## Git history requirement

**Important:** The ZIP cannot create authentic historic Git commits on your GitHub account. Commit the implementation in at least eight meaningful stages while developing/reworking it; do not fabricate timestamps or present a single upload as a development history. A suggested sequence is domain model → strategy interface → fastest → cheapest → eco → balanced → selection & constraints → runtime switching/comparison → fifth strategy → tests → UML/README. The fifth strategy should appear as a separate commit. Public repository creation, pushes and Moodle submission must be completed by the student.

## Defense talking points

Explain why `RoutePlanner` cannot determine which strategy it owns; why `RouteRules` always filters before `score()`; why weights need validation; show three calls of `setStrategy` without creating a new planner; demonstrate a budget too small to yield any valid route; explain how registration avoids a giant selector switch. Be ready to alter a score or add a strategy during the individual defense.
