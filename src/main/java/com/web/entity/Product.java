package com.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String code;

    private String alias;

    private String name;

    private String imageBanner;

    private Double price;

    private String description;

    private Date createdDate;

    private Time createdTime;

    private Integer quantitySold;

    @ManyToOne
    private Trademark trademark;

    @ManyToOne
    private Material material;

    @ManyToOne
    private Sole sole;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
    @JsonManagedReference
    private List<ProductColor> productColors = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<ProductCategory> productCategories = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<ProductImage> productImages = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductComment> productComments;
}
