package com.culturalnavigator.converter;

import com.culturalnavigator.entity.CityDistrict;
import com.culturalnavigator.repository.CityDistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityDistrictConverter implements Converter<String, CityDistrict> {

    private final CityDistrictRepository cityDistrictRepository;

    @Override
    public CityDistrict convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return cityDistrictRepository.findById(Long.valueOf(source)).orElse(null);
    }
}
