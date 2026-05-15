package com.culturalnavigator.config;

import com.culturalnavigator.converter.CategoryConverter;
import com.culturalnavigator.converter.CityDistrictConverter;
import com.culturalnavigator.converter.EventFilterModeConverter;
import com.culturalnavigator.converter.VenueConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final CategoryConverter categoryConverter;
    private final VenueConverter venueConverter;
    private final CityDistrictConverter cityDistrictConverter;
    private final EventFilterModeConverter eventFilterModeConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(categoryConverter);
        registry.addConverter(venueConverter);
        registry.addConverter(cityDistrictConverter);
        registry.addConverter(eventFilterModeConverter);
    }
}
