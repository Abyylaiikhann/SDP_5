package com.example.delivery;

public record Location(String name, double latitude, double longitude) {
    public Location {
        if (name == null || name.isBlank() || !Double.isFinite(latitude) || !Double.isFinite(longitude)
                || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180)
            throw new IllegalArgumentException("Invalid location");
    }
}
