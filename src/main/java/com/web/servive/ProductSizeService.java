package com.web.servive;

import com.web.dto.response.ProductSizeResponse;
import org.springframework.stereotype.Service;

@Service
public interface ProductSizeService {

    ProductSizeResponse findById(Long id);
}
