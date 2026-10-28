package com.example.delivery;

import java.util.List;
public final class CheapestRouteStrategy extends AbstractRouteStrategy {
    @Override public String getStrategyName() { return "Cheapest"; }
    @Override protected double score(Route r, DeliveryRequest q, List<Route> eligible) { return RouteRules.totalCost(r, q); }
    @Override protected String details(Route r, DeliveryRequest q, double s) {
        return "Total cost=" + r.cost() + " base + 12*" + r.distanceKm() +
                " distance + " + (q.priority() == DeliveryPriority.URGENT ? 500 : 0) + " urgent surcharge = " + s;
    }
}
