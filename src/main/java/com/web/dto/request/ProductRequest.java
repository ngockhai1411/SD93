package com.web.dto.request;

import com.web.entity.Material;
import com.web.entity.Sole;
import com.web.entity.Trademark;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductRequest {

    private Long id;

    @NotBlank(message = "Mã sản phẩm không được bỏ trống")
    private String code;

    private String alias;

    @NotBlank(message = "Tên sản phẩm không được bỏ trống")
    private String name;

    private String imageBanner;

    @NotNull(message = "Giá tiền không được bỏ trống")
    private Double price;

    private String description;

    private Trademark trademark;

    private Material material;

    private Sole sole;

    private List<Long> listCategoryIds = new ArrayList<>();

    private List<String> linkLinkImages = new ArrayList<>();

    private List<ColorRequest> colors = new ArrayList<>();
}
