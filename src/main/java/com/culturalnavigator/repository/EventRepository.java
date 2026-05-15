package com.culturalnavigator.repository;

import com.culturalnavigator.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
            select distinct e from Event e
            left join e.categories c
            join e.venue v
            where (:categoryId is null or c.id = :categoryId)
              and (:districtId is null or v.cityDistrict.id = :districtId)
              and (:freeOnly = false or e.price = 0)
              and (lower(e.title) like lower(concat('%', :query, '%'))
                   or lower(e.description) like lower(concat('%', :query, '%'))
                   or lower(v.name) like lower(concat('%', :query, '%'))
                   or lower(coalesce(v.cityDistrict.name, '')) like lower(concat('%', :query, '%')))
            order by e.startAt asc
            """)
    List<Event> search(
            @Param("query") String query,
            @Param("categoryId") Long categoryId,
            @Param("districtId") Long districtId,
            @Param("freeOnly") boolean freeOnly
    );

    List<Event> findByVenueIdOrderByStartAtAsc(Long venueId);

    List<Event> findByCategoriesId(Long categoryId);

    @Query("""
            select distinct e from Event e
            join e.categories c
            where coalesce((select avg(r.rating) from Review r where r.event = e), 0.0) >
                  coalesce((select avg(r2.rating) from Review r2 join r2.event.categories c2 where c2 = c), 0.0)
            """)
    List<Event> findEventsRatedAboveCategoryAverage();

    @Query("""
            select e from Event e
            where e.startAt >= :fromDate
            order by coalesce((select avg(r.rating) from Review r where r.event = e), 0.0) desc,
                     e.startAt asc
            """)
    List<Event> findPopular(@Param("fromDate") LocalDateTime fromDate);

    @Query("select coalesce(avg(r.rating), 0.0) from Review r where r.event.id = :eventId")
    Double averageRating(@Param("eventId") Long eventId);

    @Query("select count(r) from Review r where r.event.id = :eventId")
    long countReviewsByEventId(@Param("eventId") Long eventId);
}
