package com.web.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SizeRequest {

    private Long id;

    private String sizeName;

    private Integer quantity;
}
