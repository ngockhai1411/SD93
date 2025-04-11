package com.web.serviceImp;

import com.web.entity.ImportProduct;
import com.web.entity.ProductSize;
import com.web.exception.MessageException;
import com.web.repository.ImportProductRepository;
import com.web.repository.ProductSizeRepository;
import com.web.servive.ImportProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.util.Optional;

@Component
public class ImportProductServiceImp implements ImportProductService {

    @Autowired
    private ImportProductRepository importProductRepository;

    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Override
    public ImportProduct create(ImportProduct importProduct) {
        if(importProduct.getId() != null){
            throw new MessageException("id must null");
        }
        Optional<ProductSize> productSize = productSizeRepository.findById(importProduct.getProductSize().getId());
        if(productSize.isEmpty()){
            throw new MessageException("product size not found");
        }
        importProduct.setImportDate(new Date(System.currentTimeMillis()));
        importProduct.setImportTime(new Time(System.currentTimeMillis()));
        ImportProduct result = importProductRepository.save(importProduct);
        productSize.get().setQuantity(result.getQuantity() + productSize.get().getQuantity());
        productSizeRepository.save(productSize.get());
        return result;
    }

    @Override
    public ImportProduct update(ImportProduct importProduct) {
        if(importProduct.getId() == null){
            throw new MessageException("id require");
        }
        Optional<ImportProduct> exist = importProductRepository.findById(importProduct.getId());
        if(exist.isEmpty()){
            throw new MessageException("not found");
        }
        Optional<ProductSize> productSize = productSizeRepository.findById(importProduct.getProductSize().getId());
        productSize.get().setQuantity(productSize.get().getQuantity() - exist.get().getQuantity());
        importProduct.setImportDate(exist.get().getImportDate());
        importProduct.setImportTime(exist.get().getImportTime());
        ImportProduct result = importProductRepository.save(importProduct);
        productSize.get().setQuantity(productSize.get().getQuantity() + importProduct.getQuantity());
        productSizeRepository.save(productSize.get());
        return result;
    }

    @Override
    public void delete(Long id) {
        Optional<ImportProduct> exist = importProductRepository.findById(id);
        if(exist.isEmpty()){
            throw new MessageException("not found");
        }
        ProductSize productSize = exist.get().getProductSize();;
        productSize.setQuantity(productSize.getQuantity() - exist.get().getQuantity());
        if(productSize.getQuantity() < 0){
            throw new MessageException("Quantity of product size < 0 ");
        }
        productSizeRepository.save(productSize);
        importProductRepository.delete(exist.get());
    }

    @Override
    public ImportProduct findById(Long id) { Optional<ImportProduct> exist = importProductRepository.findById(id);
        if(exist.isEmpty()){
            throw new MessageException("not found");
        }
        return exist.get();
    }

    @Override
    public Page<ImportProduct> getAll(Pageable pageable) {
        Page<ImportProduct> page = importProductRepository.findAll(pageable);
        for(ImportProduct p : page.getContent()){
            p.setColorName(p.getProductSize().getProductColor().getColorName());
            p.setProductName(p.getProductSize().getProductColor().getProduct().getName());
        }
        return page;
    }

    @Override
    public Page<ImportProduct> getByProductAndDate(Long productId, Date from, Date to, Pageable pageable) {
        if(from == null || to == null){
            from = Date.valueOf("2000-01-01");
            to = Date.valueOf("2200-01-01");
        }
        System.out.println("==== from: "+from);
        System.out.println("==== to: "+to);
        Page<ImportProduct> importProducts = null;
        if(productId == null){
            importProducts = importProductRepository.findByDate(from,to, pageable);
        }
        else{
            importProducts = importProductRepository.findByDateAndProduct(from,to,productId, pageable);
        }
        for(ImportProduct p : importProducts.getContent()){
            p.setColorName(p.getProductSize().getProductColor().getColorName());
            p.setProductName(p.getProductSize().getProductColor().getProduct().getName());
        }
        return importProducts;
    }
}
