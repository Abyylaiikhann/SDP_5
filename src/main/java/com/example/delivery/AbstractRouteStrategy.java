package com.example.delivery;

import java.util.*;
public abstract class AbstractRouteStrategy implements RouteStrategy {
    protected abstract double score(Route route, DeliveryRequest request, List<Route> eligible);
    protected abstract String details(Route route, DeliveryRequest request, double score);
    @Override public RouteDecision decide(List<Route> routes, DeliveryRequest request) {
        List<Route> eligible = RouteRules.eligible(routes, request);
        Route chosen = eligible.stream().min(Comparator.comparingDouble((Route r) -> score(r, request, eligible))
                .thenComparing(Route::id)).orElseThrow();
        double value = score(chosen, request, eligible);
        return new RouteDecision(getStrategyName(), chosen, value,
                details(chosen, request, value), RouteRules.rejections(routes, request));
    }
}
