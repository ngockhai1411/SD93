package com.web.repository;

import com.web.entity.Category;
import com.web.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductSizeRepository extends JpaRepository<ProductSize,Long> {

    @Modifying
    @Transactional
    @Query(value = "delete p from product_size p inner join product_color pc on pc.id = p.product_color_id where pc.product_id = ?1", nativeQuery = true)
    int deleteByProduct(Long productId);

    @Query("select p from ProductSize p where p.productColor.id= ?1")
    public List<ProductSize> findByProductColor(Long colorId);
}
