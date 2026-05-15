package com.culturalnavigator.service;

import com.culturalnavigator.dto.VenueForm;
import com.culturalnavigator.entity.CityDistrict;
import com.culturalnavigator.entity.Venue;
import com.culturalnavigator.exception.NotFoundException;
import com.culturalnavigator.repository.CityDistrictRepository;
import com.culturalnavigator.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;
    private final CityDistrictRepository cityDistrictRepository;
    private final YandexMapsClient yandexMapsClient;

    @Transactional(readOnly = true)
    public List<Venue> findAll() {
        return venueRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Площадка не найдена"));
    }

    @CacheEvict(value = {"popularEvents", "topRoutes"}, allEntries = true)
    @Transactional
    public Venue create(VenueForm form) {
        Venue venue = new Venue();
        updateFields(venue, form);
        return venueRepository.save(venue);
    }

    @CacheEvict(value = {"popularEvents", "topRoutes"}, allEntries = true)
    @Transactional
    public Venue update(Long id, VenueForm form) {
        Venue venue = findById(id);
        updateFields(venue, form);
        return venueRepository.save(venue);
    }

    private void updateFields(Venue venue, VenueForm form) {
        venue.setName(form.getName().trim());
        venue.setDescription(form.getDescription().trim());
        venue.setAddress(form.getAddress().trim());
        CityDistrict district = form.getCityDistrictId() == null ? null : cityDistrictRepository.findById(form.getCityDistrictId())
                .orElseThrow(() -> new NotFoundException("Район не найден"));
        venue.setCityDistrict(district);
        yandexMapsClient.geocode(venue.getAddress()).ifPresent(result -> {
            venue.setLongitude(result.longitude());
            venue.setLatitude(result.latitude());
        });
    }
}
