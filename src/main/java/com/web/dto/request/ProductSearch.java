package com.web.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductSearch {

    List<Long> listIdCategory = new ArrayList<>();
    List<Long> listIdTrademark = new ArrayList<>();
}
