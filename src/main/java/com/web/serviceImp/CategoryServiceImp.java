package com.web.serviceImp;

import com.web.dto.response.CategoryResponse;
import com.web.entity.Category;
import com.web.exception.MessageException;
import com.web.mapper.CategoryMapper;
import com.web.repository.CategoryRepository;
import com.web.servive.CategoryService;
import com.web.utils.CommonPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryServiceImp implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CommonPage commonPage;

    @Override
    public Category save(Category category) {
        if(category.getId() != null){
            throw new MessageException("Id must null", 400);
        }
        if(categoryRepository.findByName(category.getName()).isPresent()){
            throw new MessageException("Category name exist");
        }
        if(category.getCategory() != null){
            if(categoryRepository.findById(category.getCategory().getId()).isEmpty()){
                throw new MessageException("Category parent: "+category.getCategory().getId()+" not found", 404);
            }
        }
        Category result = categoryRepository.save(category);
        return result;
    }

    @Override
    public Category update(Category category) {
        if(category.getId() == null){
            throw new MessageException("Id require", 400);
        }
        if(categoryRepository.findByNameAndId(category.getName(), category.getId()).isPresent()){
            throw new MessageException("Category name exist", 400);
        }
        if(category.getCategory() != null){
            if(categoryRepository.findById(category.getCategory().getId()).isEmpty()){
                throw new MessageException("Category parent: "+category.getCategory().getId()+" not found", 404);
            }
        }
        Category result = categoryRepository.save(category);
        return result;
    }

    @Override
    public void delete(Long categoryId) {
        if(categoryId == null){
            throw new MessageException("Id require", 400);
        }
        if(categoryRepository.findById(categoryId).isEmpty()){
            throw new MessageException("Not found category :"+categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryResponse findById(Long id) {
        if(categoryRepository.findById(id).isEmpty()){
            throw new MessageException("Not found category :"+id);
        }
        return categoryMapper.categoryToCategoryResponse(categoryRepository.findById(id).get());
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories;
    }

    @Override
    public Page<CategoryResponse> search(String param, Pageable pageable) {
        Page<Category> categories = categoryRepository.findByParam("%"+param+"%",pageable);
        List<CategoryResponse> list = categoryMapper.listCategoryToListCategoryResponse(categories.getContent());
        Page<CategoryResponse> result = commonPage.restPage(categories, list);
        return result;
    }

    @Override
    public List<CategoryResponse> findAllList() {
        List<Category> list = categoryRepository.findAll();
        return categoryMapper.listCategoryToListCategoryResponse(list);
    }

    @Override
    public List<CategoryResponse> findPrimary() {
        List<Category> list = categoryRepository.primaryCategory();
        return categoryMapper.listCategoryToListCategoryResponse(list);
    }

    @Override
    public List<CategoryResponse> outstanding() {
        List<Category> list = categoryRepository.outstandingCategory();
        return categoryMapper.listCategoryToListCategoryResponse(list);
    }
}
