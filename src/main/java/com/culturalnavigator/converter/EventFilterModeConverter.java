package com.culturalnavigator.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EventFilterModeConverter implements Converter<String, EventFilterMode> {

    @Override
    public EventFilterMode convert(String source) {
        if (source == null || source.isBlank()) {
            return EventFilterMode.ALL;
        }
        try {
            return EventFilterMode.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return EventFilterMode.ALL;
        }
    }
}
