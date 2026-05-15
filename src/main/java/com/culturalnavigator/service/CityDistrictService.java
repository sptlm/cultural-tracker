package com.culturalnavigator.service;

import com.culturalnavigator.entity.CityDistrict;
import com.culturalnavigator.entity.Venue;
import com.culturalnavigator.exception.NotFoundException;
import com.culturalnavigator.repository.CityDistrictRepository;
import com.culturalnavigator.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityDistrictService {

    private final CityDistrictRepository cityDistrictRepository;
    private final VenueRepository venueRepository;

    @Cacheable("districts")
    @Transactional(readOnly = true)
    public List<CityDistrict> findAll() {
        return cityDistrictRepository.findAll().stream()
                .sorted(Comparator.comparing(CityDistrict::getName))
                .toList();
    }

    @Transactional(readOnly = true)
    public CityDistrict findById(Long id) {
        return cityDistrictRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Район не найден"));
    }

    @CacheEvict(value = {"districts", "popularEvents", "topRoutes"}, allEntries = true)
    @Transactional
    public CityDistrict create(String name) {
        String normalized = normalizeName(name);
        if (cityDistrictRepository.existsByNameIgnoreCase(normalized)) {
            throw new IllegalArgumentException("Район уже существует");
        }
        CityDistrict district = new CityDistrict();
        district.setName(normalized);
        return cityDistrictRepository.save(district);
    }

    @CacheEvict(value = {"districts", "popularEvents", "topRoutes"}, allEntries = true)
    @Transactional
    public CityDistrict update(Long id, String name) {
        CityDistrict district = findById(id);
        String normalized = normalizeName(name);
        if (!district.getName().equalsIgnoreCase(normalized) && cityDistrictRepository.existsByNameIgnoreCase(normalized)) {
            throw new IllegalArgumentException("Район уже существует");
        }
        district.setName(normalized);
        return cityDistrictRepository.save(district);
    }

    @CacheEvict(value = {"districts", "popularEvents", "topRoutes"}, allEntries = true)
    @Transactional
    public void delete(Long id) {
        CityDistrict district = findById(id);
        for (Venue venue : venueRepository.findByCityDistrictIdOrderByName(id)) {
            venue.setCityDistrict(null);
        }
        cityDistrictRepository.delete(district);
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название района обязательно");
        }
        return name.trim();
    }
}
