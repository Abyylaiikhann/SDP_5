package com.example.delivery;

import java.util.Objects;
public record Route(String id, Location origin, Location destination, double distanceKm,
                    int estimatedMinutes, double cost, double carbonEmission, double trafficMultiplier,
                    int weatherPenaltyMinutes, boolean available, boolean restricted) {
    public Route {
        if (id == null || id.isBlank() || origin == null || destination == null ||
            !Double.isFinite(distanceKm) || distanceKm < 0 || estimatedMinutes < 0 ||
            !Double.isFinite(cost) || cost < 0 || !Double.isFinite(carbonEmission) || carbonEmission < 0 ||
            !Double.isFinite(trafficMultiplier) || trafficMultiplier < 1 || weatherPenaltyMinutes < 0)
            throw new IllegalArgumentException("Invalid route attributes");
    }
    public double adjustedMinutes() { return estimatedMinutes * trafficMultiplier + weatherPenaltyMinutes; }
}
