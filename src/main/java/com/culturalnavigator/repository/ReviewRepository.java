package com.culturalnavigator.repository;

import com.culturalnavigator.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByOrderByCreatedAtDesc();

    List<Review> findByEventIdOrderByCreatedAtDesc(Long eventId);

    Optional<Review> findByAuthorIdAndEventId(Long authorId, Long eventId);
}
