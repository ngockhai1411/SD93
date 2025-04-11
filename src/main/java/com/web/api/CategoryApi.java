package com.web.api;

import com.web.dto.request.CategoryRequest;
import com.web.dto.response.CategoryResponse;
import com.web.entity.Category;
import com.web.mapper.CategoryMapper;
import com.web.servive.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin
public class CategoryApi {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @PostMapping("/admin/create")
    public ResponseEntity<?> save(@Valid @RequestBody CategoryRequest categoryRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        Category category = categoryMapper.categoryRequestToCategory(categoryRequest);
        Category result = categoryService.save(category);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/admin/update")
    public ResponseEntity<?> update(@Valid @RequestBody CategoryRequest categoryRequest){
        Category category = categoryMapper.categoryRequestToCategory(categoryRequest);
        Category result = categoryService.update(category);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/delete")
    public ResponseEntity<?> delete(@RequestParam("id") Long id){
        categoryService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/public/findAll")
    public ResponseEntity<?> findAll(Pageable pageable){
        Page<Category> categories = categoryService.findAll(pageable);
        return new ResponseEntity<>(categories,HttpStatus.OK);
    }

    @GetMapping("/public/search")
    public ResponseEntity<?> search(@RequestParam("q") String search,Pageable pageable){
        Page<CategoryResponse> categories = categoryService.search(search,pageable);
        return new ResponseEntity<>(categories,HttpStatus.OK);
    }

    @GetMapping("/admin/findById")
    public ResponseEntity<?> findById(@RequestParam("id") Long id){
        CategoryResponse result = categoryService.findById(id);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/public/findAllList")
    public ResponseEntity<?> findAllList(){
        List<CategoryResponse> result = categoryService.findAllList();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/public/findPrimaryCategory")
    public ResponseEntity<?> findPrimaryCategory(){
        List<CategoryResponse> result = categoryService.findPrimary();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/public/outstanding")
    public ResponseEntity<?> outstanding(){
        List<CategoryResponse> result = categoryService.outstanding();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
