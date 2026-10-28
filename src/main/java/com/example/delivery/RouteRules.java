package com.example.delivery;

import java.util.*;
public final class RouteRules {
    private RouteRules() {}
    public static String rejectionReason(Route route, DeliveryRequest request) {
        Objects.requireNonNull(route, "route"); Objects.requireNonNull(request, "request");
        if (!route.available()) return "unavailable";
        if (route.restricted()) return "restricted";
        if (totalCost(route, request) > request.maxBudget() + 1e-9) return "budget exceeded";
        if (route.adjustedMinutes() > request.maxDeliveryMinutes() + 1e-9) return "deadline exceeded";
        if (route.carbonEmission() > request.maxEmission() + 1e-9) return "emission cap exceeded";
        return null;
    }
    // base price + 12 currency units per km + priority surcharge + tolls represented by base price
    public static double totalCost(Route route, DeliveryRequest request) {
        return route.cost() + 12 * route.distanceKm() + (request.priority() == DeliveryPriority.URGENT ? 500 : 0);
    }
    public static List<Route> eligible(List<Route> routes, DeliveryRequest request) {
        Objects.requireNonNull(routes, "routes"); Objects.requireNonNull(request, "request");
        List<Route> result = new ArrayList<>();
        for (Route route : routes) if (rejectionReason(route, request) == null) result.add(route);
        if (result.isEmpty()) throw new NoSuitableRouteException("No routes satisfy budget, deadline, emissions and availability constraints");
        return List.copyOf(result);
    }
    public static List<String> rejections(List<Route> routes, DeliveryRequest request) {
        List<String> reasons = new ArrayList<>();
        for (Route route : routes) {
            String reason = rejectionReason(route, request);
            if (reason != null) reasons.add(route.id() + ": " + reason);
        }
        return reasons;
    }
}
