package com.web.repository;

import com.web.entity.Category;
import com.web.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product> {

    @Query("select p from Product p where p.name = ?1")
    public Optional<Product> findByName(String name);

    @Query("select p from Product p where p.id = ?1")
    public Optional<Product> findById(Long id);

    @Query("select p from Product p where p.alias = ?1")
    public Optional<Product> findByAlias(String alias);

    @Query("select p from Product p")
    public Page<Product> findAll(Pageable pageable);

    @Query("select p from Product p where p.name like ?1")
    public Page<Product> findAllByParam(String param, Pageable pageable);

    @Query("select p from Product p where p.name like ?1 or p.code like ?1")
    public List<Product> findByParam(String param);

    @Query("select distinct p from Product p inner join p.productCategories pc where pc.category.id = ?1")
    public Page<Product> findByCategory(Long category, Pageable pageable);

    @Query(value = "SELECT \n" +
            "     \n" +
            "    (\n" +
            "        SELECT SUM(ivdt.quantity) \n" +
            "        FROM invoice_detail ivdt\n" +
            "        INNER JOIN product_size pds ON pds.id = ivdt.product_size_id\n" +
            "        INNER JOIN product_color pdc ON pdc.id = pds.product_color_id\n" +
            "        INNER JOIN invoice i ON i.id = ivdt.invoice_id\n" +
            "        WHERE MONTH(i.created_date) = Month(CURRENT_DATE) \n" +
            "          AND YEAR(i.created_date) = Year(CURRENT_DATE) \n" +
            "          AND pdc.product_id = p.id\n" +
            "    ) AS quantity_sold, p.*\n" +
            "FROM product p\n" +
            "INNER JOIN product_color pc ON pc.product_id = p.id\n" +
            "INNER JOIN product_size ps ON ps.product_color_id = pc.id\n" +
            "INNER JOIN invoice_detail iv ON iv.product_size_id = ps.id\n" +
            "GROUP BY p.id\n" +
            "HAVING quantity_sold IS NOT NULL\n" +
            "ORDER BY quantity_sold DESC limit 10\n", nativeQuery = true)
    public List<Product> sanPhamBanChayThangNay();

}
