package com.example.delivery;

import java.util.List;
public final class DemoData {
    private DemoData() {}
    public static List<Route> routes() {
        Location warehouse = new Location("Astana warehouse", 51.1694, 71.4491);
        Location customer = new Location("Customer", 51.1282, 71.4304);
        return List.of(
            new Route("R1",warehouse,customer,14,25,3700,3.2,1.50,5,true,false),
            new Route("R2",warehouse,customer,18,22,4700,4.0,1.05,0,true,false),
            new Route("R3",warehouse,customer,11,37,2800,1.8,1.10,2,true,false),
            new Route("R4",warehouse,customer,15,42,2700,0.9,1.00,0,true,false),
            new Route("R5",warehouse,customer,13,45,1900,2.5,1.10,2,true,false),
            new Route("R6",warehouse,customer,9,30,5000,1.5,1.10,0,true,false),
            new Route("R7",warehouse,customer,8,20,1200,0.5,1.00,0,false,false),
            new Route("R8",warehouse,customer,10,21,1500,0.7,1.00,0,true,true)
        );
    }
}
