package com.culturalnavigator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "venues")
public class Venue extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(name = "city_district_id")
    private CityDistrict cityDistrict;

    private Double latitude;

    private Double longitude;

    @OneToMany(mappedBy = "venue")
    private List<Event> events = new ArrayList<>();
}
