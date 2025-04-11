package com.web.servive;

import com.web.dto.request.ProductRequest;
import com.web.dto.response.ProductResponse;
import com.web.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface ProductService {

    public ProductResponse save(ProductRequest productRequest);

    public ProductResponse update(ProductRequest productRequest);

    public ProductResponse delete(Long idProduct);

    public Page<ProductResponse> findAll(Pageable pageable);

    public List<ProductResponse> findAllList(String search);

    public Page<ProductResponse> search(String param,Pageable pageable);

    public Page<ProductResponse> findByCategory(Long idCategory,Pageable pageable);

    public Page<ProductResponse> searchFull(Double smallPrice, Double largePrice, List<Long> listIdCategory, Pageable pageable);

    public Page<ProductResponse> searchFullProduct(Double smallPrice, Double largePrice, List<Long> listIdCategory,List<Long> listTrademark, Pageable pageable);

    public ProductResponse findByIdForAdmin(Long id);

    public ProductResponse findByIdForUser(Long id);

    public ProductResponse findByAlias(String alias);

    public List<Product> findByImage(MultipartFile multipartFile) throws IOException;

    public List<ProductResponse> sanPhamBanChayThangNay() ;
}
