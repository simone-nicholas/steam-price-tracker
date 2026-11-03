package com.company.steampricetracker.Steam.dto;

public record GameDataDto(
        String currency,
        Integer initial,
        Integer current,
        Integer discountPercent,
        String initialFormatted,
        String finalFormatted
) {
}
