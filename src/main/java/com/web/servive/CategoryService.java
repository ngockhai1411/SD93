package com.web.servive;

import com.web.dto.response.CategoryResponse;
import com.web.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    public Category save(Category category);

    public Category update(Category category);

    public void delete(Long categoryId);

    public CategoryResponse findById(Long id);

    public Page<Category> findAll(Pageable pageable);

    public Page<CategoryResponse> search(String param, Pageable pageable);

    public List<CategoryResponse> findAllList();

    public List<CategoryResponse> findPrimary();

    public List<CategoryResponse> outstanding();
}
