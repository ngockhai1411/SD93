package com.web.dto.request;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.web.entity.Category;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Index;
import org.springframework.stereotype.Indexed;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CategoryRequest {

    private Long id;

    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    private Boolean isPrimary;

    private String imageBanner;

    private Category category;
}
