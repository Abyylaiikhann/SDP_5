package com.example.delivery;

import java.util.List;
public final class FastestRouteStrategy extends AbstractRouteStrategy {
    @Override public String getStrategyName() { return "Fastest"; }
    @Override protected double score(Route r, DeliveryRequest q, List<Route> eligible) { return r.adjustedMinutes(); }
    @Override protected String details(Route r, DeliveryRequest q, double s) {
        return "Base time=" + r.estimatedMinutes() + " min; traffic x" + r.trafficMultiplier() +
                "; weather +" + r.weatherPenaltyMinutes() + " min; final=" + s + " min";
    }
}
