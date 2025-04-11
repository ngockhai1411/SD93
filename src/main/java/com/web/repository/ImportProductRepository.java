package com.web.repository;

import com.web.entity.Category;
import com.web.entity.ImportProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

public interface ImportProductRepository extends JpaRepository<ImportProduct,Long> {


    @Query("select i from ImportProduct i where i.importDate >= ?1 and i.importDate <= ?2")
    public Page<ImportProduct> findByDate(Date from, Date to, Pageable pageable);

    @Query("select i from ImportProduct i where i.importDate >= ?1 and i.importDate <= ?2 and i.productSize.productColor.product.id = ?3")
    public Page<ImportProduct> findByDateAndProduct(Date from, Date to, Long idProduct, Pageable pageable);


    @Modifying
    @Transactional
    @Query(value = "delete i from import_product i inner join product_size p on p.id = i.product_size_id inner join product_color pc on pc.id = p.product_color_id where pc.product_id = ?1", nativeQuery = true)
    int deleteByProduct(Long productId);
}
