package com.culturalnavigator.repository;

import com.culturalnavigator.entity.CityDistrict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityDistrictRepository extends JpaRepository<CityDistrict, Long> {
    Optional<CityDistrict> findByName(String name);

    boolean existsByNameIgnoreCase(String name);
}
