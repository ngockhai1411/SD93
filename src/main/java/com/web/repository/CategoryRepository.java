package com.web.repository;

import com.web.entity.Authority;
import com.web.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Query("select c from Category c where c.name = ?1")
    public Optional<Category> findByName(String name);

    @Query("select c from Category c where c.name = ?1 and c.id <> ?2")
    public Optional<Category> findByNameAndId(String name, Long id);

    @Query("select c from Category c where c.name like ?1")
    public Page<Category> findByParam(String param, Pageable pageable);

    @Query("select c from Category c where c.isPrimary = true")
    public List<Category> primaryCategory();


    @Query(value = "SELECT c.*,\n" +
            "(SELECT count(pc.id) from product_category pc where pc.category_id = c.id) as quantity\n" +
            "from category c group by c.id order by quantity desc limit 4", nativeQuery = true)
    public List<Category> outstandingCategory();
}
