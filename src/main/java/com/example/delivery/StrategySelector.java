package com.example.delivery;

import java.util.*;
public final class StrategySelector {
    private final Map<DeliveryPriority, RouteStrategy> strategies = new EnumMap<>(DeliveryPriority.class);
    public StrategySelector register(DeliveryPriority priority, RouteStrategy strategy) {
        strategies.put(Objects.requireNonNull(priority), Objects.requireNonNull(strategy)); return this;
    }
    public RouteStrategy select(DeliveryRequest request) {
        Objects.requireNonNull(request);
        // Eco preference overrides STANDARD but not an explicit URGENT or ECONOMY priority.
        DeliveryPriority priority = request.ecoPreferred() && request.priority() == DeliveryPriority.STANDARD
                ? DeliveryPriority.ECO : request.priority();
        RouteStrategy strategy = strategies.get(priority);
        if (strategy == null) throw new IllegalStateException("No strategy registered for " + priority);
        return strategy;
    }
}
