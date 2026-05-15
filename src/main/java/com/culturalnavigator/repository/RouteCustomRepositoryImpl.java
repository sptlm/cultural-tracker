package com.culturalnavigator.repository;

import com.culturalnavigator.entity.Route;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RouteCustomRepositoryImpl implements RouteCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Route> findByBudgetRangeDurationAndMinItems(BigDecimal minBudget, BigDecimal maxBudget, Integer maxDurationMinutes, int minItems) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Route> cq = cb.createQuery(Route.class);
        Root<Route> route = cq.from(Route.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isTrue(route.get("isPublic")));
        predicates.add(cb.greaterThanOrEqualTo(cb.size(route.get("items")), minItems));
        if (minBudget != null) {
            predicates.add(cb.greaterThanOrEqualTo(route.get("budget"), minBudget));
        }
        if (maxBudget != null) {
            predicates.add(cb.lessThanOrEqualTo(route.get("budget"), maxBudget));
        }
        if (maxDurationMinutes != null) {
            predicates.add(cb.lessThanOrEqualTo(route.get("durationMinutes"), maxDurationMinutes));
        }
        cq.select(route).where(predicates.toArray(Predicate[]::new)).orderBy(cb.desc(route.get("createdAt")));
        return entityManager.createQuery(cq).getResultList();
    }
}
