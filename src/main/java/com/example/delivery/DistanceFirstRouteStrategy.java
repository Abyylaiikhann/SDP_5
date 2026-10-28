package com.example.delivery;

import java.util.List;
// Fifth strategy added independently: context and the original algorithms are unchanged.
public final class DistanceFirstRouteStrategy extends AbstractRouteStrategy {
    @Override public String getStrategyName() { return "Distance-first"; }
    @Override protected double score(Route r, DeliveryRequest q, List<Route> eligible) { return r.distanceKm(); }
    @Override protected String details(Route r, DeliveryRequest q, double s) { return "Minimum feasible distance=" + s + " km"; }
}
