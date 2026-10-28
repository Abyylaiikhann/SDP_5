package com.example.delivery;

import java.util.*;
public final class Main {
    public static void main(String[] args) {
        RouteStrategy fastest = new FastestRouteStrategy();
        RouteStrategy cheapest = new CheapestRouteStrategy();
        RouteStrategy eco = new EcoFriendlyRouteStrategy();
        RouteStrategy balanced = new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2);
        RouteStrategy distance = new DistanceFirstRouteStrategy();
        List<RouteStrategy> all = List.of(fastest, cheapest, eco, balanced, distance);
        StrategySelector selector = new StrategySelector().register(DeliveryPriority.URGENT, fastest)
                .register(DeliveryPriority.ECONOMY, cheapest).register(DeliveryPriority.ECO, eco)
                .register(DeliveryPriority.STANDARD, balanced);
        DeliveryRequest request = new DeliveryRequest("105", DeliveryPriority.STANDARD, 10000, 90, false, 10);
        RoutePlanner planner = new RoutePlanner(selector.select(request));
        System.out.println("DELIVERY REQUEST #" + request.id());
        System.out.println("Automatic selection: " + planner.plan(DemoData.routes(), request).strategyName());
        // Same Context instance, four algorithms = three runtime switches.
        for (RouteStrategy next : all) {
            planner.setStrategy(next);
            print(planner.plan(DemoData.routes(), request));
        }
        System.out.println("--- STRATEGY COMPARISON ---");
        new ComparisonService(all).compare(DemoData.routes(), request).forEach(Main::print);
        for (DeliveryPriority p : DeliveryPriority.values()) {
            DeliveryRequest r = new DeliveryRequest(p.name(), p, 6000, 65, false, 5);
            planner.setStrategy(selector.select(r));
            System.out.print("Auto " + p + ": "); print(planner.plan(DemoData.routes(), r));
        }
    }
    private static void print(RouteDecision d) {
        System.out.printf(Locale.US, "%s -> %s | time %.1f min | cost %.2f | CO2 %.2f kg | score %.4f%n",
                d.strategyName(), d.route().id(), d.route().adjustedMinutes(),
                RouteRules.totalCost(d.route(), new DeliveryRequest("view", DeliveryPriority.STANDARD,
                        Double.POSITIVE_INFINITY, Integer.MAX_VALUE, false, Double.POSITIVE_INFINITY)),
                d.route().carbonEmission(), d.score());
        System.out.println("  " + d.explanation());
        System.out.println("  Rejected: " + String.join("; ", d.rejectedRoutes()));
    }
}
