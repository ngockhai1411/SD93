package com.web.dto.response;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.web.entity.Category;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CategoryResponse {

    private Long id;

    private String name;

    private String imageBanner;

    private Boolean isPrimary;

    private String categoryParentName;

    private Long categoryParentId;

    private List<Category> categories = new ArrayList<>();
}
