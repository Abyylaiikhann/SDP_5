package com.example.delivery;

import java.util.List;
public interface RouteStrategy {
    RouteDecision decide(List<Route> routes, DeliveryRequest request);
    String getStrategyName();
    default Route selectRoute(List<Route> routes, DeliveryRequest request) { return decide(routes, request).route(); }
}
