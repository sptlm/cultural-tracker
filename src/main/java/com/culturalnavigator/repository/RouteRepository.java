package com.culturalnavigator.repository;

import com.culturalnavigator.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long>, RouteCustomRepository {
    List<Route> findByIsPublicTrueOrderByCreatedAtDesc();

    List<Route> findByAuthorUsernameOrderByCreatedAtDesc(String username);

    @Query("""
            select r from Route r
            where r.isPublic = true
               or r.author.username = :username
            order by r.createdAt desc
            """)
    List<Route> findVisibleForUsername(String username);

    @Query("""
            select r from Route r
            where r.isPublic = true
              and exists (
                  select i from RouteItem i
                  where i.route = r
                    and coalesce((select avg(rv.rating) from Review rv where rv.event = i.event), 0.0) >= 4.0
              )
            order by r.createdAt desc
            """)
    List<Route> findPublicRoutesWithHighlyRatedEvents();
}
