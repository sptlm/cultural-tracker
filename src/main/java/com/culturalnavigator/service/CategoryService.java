package com.culturalnavigator.service;

import com.culturalnavigator.entity.Category;
import com.culturalnavigator.entity.Event;
import com.culturalnavigator.exception.NotFoundException;
import com.culturalnavigator.repository.CategoryRepository;
import com.culturalnavigator.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Cacheable("categories")
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
    }

    @CacheEvict(value = {"categories", "popularEvents", "topRoutes"}, allEntries = true)
    @Transactional
    public Category create(String name) {
        String normalized = normalizeName(name);
        if (categoryRepository.existsByNameIgnoreCase(normalized)) {
            throw new IllegalArgumentException("Категория уже существует");
        }
        Category category = new Category();
        category.setName(normalized);
        return categoryRepository.save(category);
    }

    @CacheEvict(value = {"categories", "popularEvents", "topRoutes"}, allEntries = true)
    @Transactional
    public Category update(Long id, String name) {
        Category category = findById(id);
        String normalized = normalizeName(name);
        if (!category.getName().equalsIgnoreCase(normalized) && categoryRepository.existsByNameIgnoreCase(normalized)) {
            throw new IllegalArgumentException("Категория уже существует");
        }
        category.setName(normalized);
        return categoryRepository.save(category);
    }

    @CacheEvict(value = {"categories", "popularEvents", "topRoutes"}, allEntries = true)
    @Transactional
    public void delete(Long id) {
        Category category = findById(id);
        for (Event event : eventRepository.findByCategoriesId(id)) {
            event.getCategories().remove(category);
        }
        categoryRepository.delete(category);
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название категории обязательно");
        }
        return name.trim();
    }
}
