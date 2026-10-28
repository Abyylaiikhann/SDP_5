package com.example.delivery;

import java.util.List;
public final class EcoFriendlyRouteStrategy extends AbstractRouteStrategy {
    @Override public String getStrategyName() { return "Eco-friendly"; }
    @Override protected double score(Route r, DeliveryRequest q, List<Route> eligible) { return r.carbonEmission() * 0.8 + r.distanceKm() * 0.2; }
    @Override protected String details(Route r, DeliveryRequest q, double s) {
        return "Eco score=0.8*" + r.carbonEmission() + " kg CO2 + 0.2*" + r.distanceKm() + " km = " + s;
    }
}
