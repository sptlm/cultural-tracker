package com.culturalnavigator.converter;

import com.culturalnavigator.entity.Category;
import com.culturalnavigator.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryConverter implements Converter<String, Category> {

    private final CategoryRepository categoryRepository;

    @Override
    public Category convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return categoryRepository.findById(Long.valueOf(source)).orElse(null);
    }
}
