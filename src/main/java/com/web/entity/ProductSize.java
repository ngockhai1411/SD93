package com.web.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "product_size")
@Getter
@Setter
public class ProductSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String sizeName;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "product_color_id")
    @JsonBackReference
    private ProductColor productColor;

    @OneToMany(mappedBy = "productSize", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ImportProduct> importProducts;
}
