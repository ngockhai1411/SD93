package com.web.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Tên danh mục không được để trống")
    @NotNull(message = "Tên danh mục không được để trống")
    private String name;

    private Boolean isPrimary;

    private String imageBanner;

    @ManyToOne
    @JoinColumn(name = "category_parent")
    @JsonBackReference
    private Category category;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<Category> categories = new ArrayList<>();
}
