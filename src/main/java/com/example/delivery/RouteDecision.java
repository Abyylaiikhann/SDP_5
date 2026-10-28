package com.example.delivery;

import java.util.List;
public record RouteDecision(String strategyName, Route route, double score, String explanation,
                            List<String> rejectedRoutes) {
    public RouteDecision { rejectedRoutes = List.copyOf(rejectedRoutes); }
}
