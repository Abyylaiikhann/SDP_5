package com.example.delivery;

import java.util.List;
public final class BalancedRouteStrategy extends AbstractRouteStrategy {
    private final double timeWeight, costWeight, distanceWeight, emissionWeight;
    public BalancedRouteStrategy(double timeWeight, double costWeight, double distanceWeight, double emissionWeight) {
        double[] w = {timeWeight, costWeight, distanceWeight, emissionWeight};
        double sum = 0;
        for (double x : w) { if (!Double.isFinite(x) || x < 0 || x > 1) throw new IllegalArgumentException("Weights must be in [0, 1]"); sum += x; }
        if (Math.abs(sum - 1) > 1e-9) throw new IllegalArgumentException("Weights must sum to 1.0");
        this.timeWeight = timeWeight; this.costWeight = costWeight;
        this.distanceWeight = distanceWeight; this.emissionWeight = emissionWeight;
    }
    @Override public String getStrategyName() { return "Balanced"; }
    private static double max(List<Route> routes, java.util.function.ToDoubleFunction<Route> f) {
        return Math.max(1e-9, routes.stream().mapToDouble(f).max().orElse(1));
    }
    @Override protected double score(Route r, DeliveryRequest q, List<Route> eligible) {
        // Normalize against the maximum *eligible* value; zero safely remains zero.
        double maxTime = max(eligible, Route::adjustedMinutes);
        double maxCost = max(eligible, x -> RouteRules.totalCost(x, q));
        double maxDistance = max(eligible, Route::distanceKm);
        double maxEmission = max(eligible, Route::carbonEmission);
        return timeWeight * r.adjustedMinutes() / maxTime
                + costWeight * RouteRules.totalCost(r, q) / maxCost
                + distanceWeight * r.distanceKm() / maxDistance
                + emissionWeight * r.carbonEmission() / maxEmission;
    }
    @Override protected String details(Route r, DeliveryRequest q, double s) {
        return "Weighted normalized time/cost/distance/CO2, weights=[" + timeWeight + ", " + costWeight
                + ", " + distanceWeight + ", " + emissionWeight + "]; score=" + String.format(java.util.Locale.US,"%.4f",s);
    }
}
