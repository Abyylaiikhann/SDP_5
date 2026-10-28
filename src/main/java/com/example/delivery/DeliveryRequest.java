package com.example.delivery;

public record DeliveryRequest(String id, DeliveryPriority priority, double maxBudget,
                              int maxDeliveryMinutes, boolean ecoPreferred, double maxEmission) {
    public DeliveryRequest {
        if (id == null || id.isBlank() || priority == null ||
            Double.isNaN(maxBudget) || maxBudget < 0 || maxDeliveryMinutes < 0 ||
            Double.isNaN(maxEmission) || maxEmission < 0)
            throw new IllegalArgumentException("Invalid delivery request");
    }
}
