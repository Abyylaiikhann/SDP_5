package com.example.delivery;

import java.util.*;
public final class ComparisonService {
    private final List<RouteStrategy> strategies;
    public ComparisonService(List<RouteStrategy> strategies) { this.strategies = List.copyOf(strategies); }
    public List<RouteDecision> compare(List<Route> routes, DeliveryRequest request) {
        List<RouteDecision> decisions = new ArrayList<>();
        for (RouteStrategy strategy : strategies) decisions.add(strategy.decide(routes, request));
        return List.copyOf(decisions);
    }
}
