package com.culturalnavigator.repository;

import com.culturalnavigator.entity.Route;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RouteCustomRepositoryImpl implements RouteCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Route> findByBudgetRangeAndMinItems(int minItems) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Route> cq = cb.createQuery(Route.class);
        Root<Route> route = cq.from(Route.class);
        cq.select(route).where(cb.greaterThanOrEqualTo(cb.size(route.get("items")), minItems));
        return entityManager.createQuery(cq).getResultList();
    }
}
