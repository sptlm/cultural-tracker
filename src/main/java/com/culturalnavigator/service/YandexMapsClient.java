package com.culturalnavigator.service;

import com.culturalnavigator.config.AppProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class YandexMapsClient {

    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;
    private final OkHttpClient okHttpClient = new OkHttpClient();

    public Optional<GeocodingResult> geocode(String address) {
        String geocoderApiKey = appProperties.getYandex().getGeocoderApiKey();
        if (geocoderApiKey == null || geocoderApiKey.isBlank() || address == null || address.isBlank()) {
            return Optional.empty();
        }

        HttpUrl url = HttpUrl.parse("https://geocode-maps.yandex.ru/v1/")
                .newBuilder()
                .addQueryParameter("apikey", geocoderApiKey)
                .addQueryParameter("geocode", address)
                .addQueryParameter("lang", "ru_RU")
                .addQueryParameter("format", "json")
                .build();

        Request request = new Request.Builder().url(url).get().build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                log.warn("Yandex geocoder failed with code {}", response.code());
                return Optional.empty();
            }
            JsonNode root = objectMapper.readTree(response.body().string());
            String pos = root.path("response").path("GeoObjectCollection")
                    .path("featureMember").path(0).path("GeoObject").path("Point").path("pos").asText("");
            String[] parts = pos.split(" ");
            if (parts.length != 2) {
                return Optional.empty();
            }
            return Optional.of(new GeocodingResult(Double.parseDouble(parts[0]), Double.parseDouble(parts[1])));
        } catch (IOException | RuntimeException ex) {
            log.warn("Yandex geocoder request failed", ex);
            return Optional.empty();
        }
    }

    public double estimateDistanceKm(List<GeocodingResult> points) {
        if (points == null || points.size() < appProperties.getGeo().getMinRoutePoints()) {
            return 0;
        }
        double result = 0;
        for (int i = 1; i < points.size(); i++) {
            result += haversine(points.get(i - 1), points.get(i));
        }
        return roundDistance(result);
    }

    public double distanceKm(GeocodingResult from, GeocodingResult to) {
        return roundDistance(haversine(from, to));
    }

    private double haversine(GeocodingResult from, GeocodingResult to) {
        double dLat = Math.toRadians(to.latitude() - from.latitude());
        double dLon = Math.toRadians(to.longitude() - from.longitude());
        double lat1 = Math.toRadians(from.latitude());
        double lat2 = Math.toRadians(to.latitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * appProperties.getGeo().getEarthRadiusKm() * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private double roundDistance(double distanceKm) {
        double multiplier = Math.pow(10, appProperties.getGeo().getDistanceScale());
        return Math.round(distanceKm * multiplier) / multiplier;
    }
}
