package com.culturalnavigator.converter;

import com.culturalnavigator.entity.Venue;
import com.culturalnavigator.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VenueConverter implements Converter<String, Venue> {

    private final VenueRepository venueRepository;

    @Override
    public Venue convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return venueRepository.findById(Long.valueOf(source)).orElse(null);
    }
}
