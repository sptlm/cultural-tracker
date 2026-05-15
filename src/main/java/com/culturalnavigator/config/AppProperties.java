package com.culturalnavigator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Yandex yandex = new Yandex();
    private Money money = new Money();
    private Events events = new Events();
    private Recommendations recommendations = new Recommendations();
    private Geo geo = new Geo();
    private Routes routes = new Routes();
    private Ui ui = new Ui();
    private Cache cache = new Cache();

    @Getter
    @Setter
    public static class Yandex {
        private String geocoderApiKey = "";
    }

    @Getter
    @Setter
    public static class Money {
        private BigDecimal max = new BigDecimal("1000000");
    }

    @Getter
    @Setter
    public static class Events {
        private int popularLimit = 6;
        private int popularLookbackDays = 1;
        private int eveningStartsAtHour = 18;
        private double nearbyDistanceKm = 5.0;
        private double highRatingThreshold = 4.5;
    }

    @Getter
    @Setter
    public static class Recommendations {
        private int favoriteCategoryScore = 100;
        private int budgetScore = 50;
        private double nearDistanceKm = 2.0;
        private int nearDistanceScore = 30;
        private double mediumDistanceKm = 5.0;
        private int mediumDistanceScore = 15;
    }

    @Getter
    @Setter
    public static class Geo {
        private double earthRadiusKm = 6371.0;
        private int distanceScale = 1;
        private int minRoutePoints = 2;
    }

    @Getter
    @Setter
    public static class Routes {
        private int homeLimit = 3;
        private int defaultApiDurationMinutes = 60;
    }

    @Getter
    @Setter
    public static class Ui {
        private int toastDurationMs = 4500;
        private int actionToastDurationMs = 8000;
    }

    @Getter
    @Setter
    public static class Cache {
        private Duration ttl = Duration.ofMinutes(20);
    }
}
