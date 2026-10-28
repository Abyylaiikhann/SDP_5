package com.example.delivery;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class DeliveryEngineTest {
    private final List<Route> routes = DemoData.routes();
    private DeliveryRequest request(DeliveryPriority p, double budget, int deadline, double emissions) {
        return new DeliveryRequest("test", p, budget, deadline, false, emissions);
    }
    private DeliveryRequest unrestricted() { return request(DeliveryPriority.STANDARD, 100000, 1000, 1000); }
    @Test void fastestAccountsForTrafficAndWeather() { assertEquals("R2", new FastestRouteStrategy().selectRoute(routes, unrestricted()).id()); }
    @Test void cheapestIncludesDistanceCharges() { assertEquals("R5", new CheapestRouteStrategy().selectRoute(routes, unrestricted()).id()); }
    @Test void ecoConsidersEmissionsAndDistance() { assertEquals("R6", new EcoFriendlyRouteStrategy().selectRoute(routes, unrestricted()).id()); }
    @Test void balancedReturnsEligibleCandidate() { assertTrue(routes.contains(new BalancedRouteStrategy(.4,.3,.1,.2).selectRoute(routes, unrestricted()))); }
    @Test void distanceFirstChoosesShortestAvailableUnrestricted() { assertEquals("R6", new DistanceFirstRouteStrategy().selectRoute(routes, unrestricted()).id()); }
    @Test void runtimeSwitchesOnSamePlanner() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        assertEquals("R2", planner.plan(routes, unrestricted()).route().id());
        planner.setStrategy(new CheapestRouteStrategy()); assertEquals("R5", planner.plan(routes, unrestricted()).route().id());
        planner.setStrategy(new EcoFriendlyRouteStrategy()); assertEquals("R6", planner.plan(routes, unrestricted()).route().id());
        planner.setStrategy(new DistanceFirstRouteStrategy()); assertEquals("R6", planner.plan(routes, unrestricted()).route().id());
    }
    private StrategySelector selector() { return new StrategySelector()
            .register(DeliveryPriority.URGENT, new FastestRouteStrategy())
            .register(DeliveryPriority.ECONOMY, new CheapestRouteStrategy())
            .register(DeliveryPriority.ECO, new EcoFriendlyRouteStrategy())
            .register(DeliveryPriority.STANDARD, new BalancedRouteStrategy(.4,.3,.1,.2)); }
    @Test void automaticUrgent() { assertInstanceOf(FastestRouteStrategy.class, selector().select(request(DeliveryPriority.URGENT,100000,1000,1000))); }
    @Test void automaticEconomy() { assertInstanceOf(CheapestRouteStrategy.class, selector().select(request(DeliveryPriority.ECONOMY,100000,1000,1000))); }
    @Test void automaticEco() { assertInstanceOf(EcoFriendlyRouteStrategy.class, selector().select(request(DeliveryPriority.ECO,100000,1000,1000))); }
    @Test void standardWithEcoPreference() { assertInstanceOf(EcoFriendlyRouteStrategy.class, selector().select(new DeliveryRequest("eco",DeliveryPriority.STANDARD,10000,100,true,10))); }
    @Test void unavailableAndRestrictedAreNeverChosen() { assertFalse(Set.of("R7","R8").contains(new FastestRouteStrategy().selectRoute(routes, unrestricted()).id())); }
    @Test void budgetRestriction() { Route r = new CheapestRouteStrategy().selectRoute(routes, request(DeliveryPriority.ECONOMY,2100,1000,1000)); assertEquals("R5",r.id()); }
    @Test void deadlineRestriction() { assertEquals("R2",new FastestRouteStrategy().selectRoute(routes,request(DeliveryPriority.STANDARD,100000,25,1000)).id()); }
    @Test void emissionRestriction() { assertEquals("R4",new EcoFriendlyRouteStrategy().selectRoute(routes,request(DeliveryPriority.STANDARD,100000,1000,1)).id()); }
    @Test void noValidRouteThrows() { assertThrows(NoSuitableRouteException.class, () -> new CheapestRouteStrategy().selectRoute(routes, request(DeliveryPriority.STANDARD,10,10,0))); }
    @Test void emptyRoutesThrows() { assertThrows(NoSuitableRouteException.class, () -> new FastestRouteStrategy().selectRoute(List.of(), unrestricted())); }
    @Test void invalidWeightsSumThrows() { assertThrows(IllegalArgumentException.class, () -> new BalancedRouteStrategy(.4,.3,.2,.2)); }
    @Test void negativeWeightThrows() { assertThrows(IllegalArgumentException.class, () -> new BalancedRouteStrategy(-.1,.5,.3,.3)); }
    @Test void comparisonRunsAllFive() { assertEquals(5,new ComparisonService(List.of(new FastestRouteStrategy(),new CheapestRouteStrategy(),new EcoFriendlyRouteStrategy(),new BalancedRouteStrategy(.4,.3,.1,.2),new DistanceFirstRouteStrategy())).compare(routes,unrestricted()).size()); }
    @Test void comparisonHasDifferentDecisions() { var results = new ComparisonService(List.of(new FastestRouteStrategy(),new CheapestRouteStrategy())).compare(routes,unrestricted()); assertNotEquals(results.get(0).route().id(),results.get(1).route().id()); }
    @Test void budgetBoundaryAccepted() { Route r=routes.get(4); double limit=RouteRules.totalCost(r,unrestricted()); assertNull(RouteRules.rejectionReason(r,request(DeliveryPriority.STANDARD,limit,1000,1000))); }
    @Test void deadlineBoundaryAccepted() { Route r=routes.get(1); assertNull(RouteRules.rejectionReason(r,request(DeliveryPriority.STANDARD,100000,(int)r.adjustedMinutes(),1000))); }
    @Test void negativeRouteCostRejected() { assertThrows(IllegalArgumentException.class, () -> new Route("bad",routes.get(0).origin(),routes.get(0).destination(),5,20,-1,1,1,0,true,false)); }
    @Test void nullRequestRejected() { assertThrows(NullPointerException.class, () -> new FastestRouteStrategy().selectRoute(routes,null)); }
    @Test void explanationListsRejections() { var d = new FastestRouteStrategy().decide(routes,unrestricted()); assertTrue(d.rejectedRoutes().stream().anyMatch(s -> s.contains("R7"))); assertTrue(d.explanation().contains("traffic")); }
    @Test void prioritySurchargeApplies() { assertEquals(RouteRules.totalCost(routes.get(0),unrestricted())+500,RouteRules.totalCost(routes.get(0),request(DeliveryPriority.URGENT,100000,1000,1000))); }
}
