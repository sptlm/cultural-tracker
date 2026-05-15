package com.culturalnavigator.repository;

import com.culturalnavigator.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findByCityDistrictIdOrderByName(Long cityDistrictId);

    List<Venue> findAllByOrderByNameAsc();
}
