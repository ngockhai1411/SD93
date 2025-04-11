package com.web.repository;

import com.web.entity.Category;
import com.web.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Long> {

    @Modifying
    @Transactional
    @Query("delete from ProductCategory p where p.product.id = ?1")
    int deleteByProduct(Long productId);
}
