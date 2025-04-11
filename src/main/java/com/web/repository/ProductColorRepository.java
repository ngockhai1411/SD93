package com.web.repository;

import com.web.entity.Category;
import com.web.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProductColorRepository extends JpaRepository<ProductColor,Long> {

    @Modifying
    @Transactional
    @Query("delete from ProductColor p where p.product.id = ?1")
    int deleteByProduct(Long productId);

    @Modifying
    @Transactional
    @Query("update ProductColor p set p.product = null where p.product.id = ?1")
    int setNull(Long productId);
}
