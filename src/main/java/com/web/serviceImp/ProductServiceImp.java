package com.web.serviceImp;

import com.web.dto.request.ColorRequest;
import com.web.dto.request.ProductRequest;
import com.web.dto.request.SizeRequest;
import com.web.dto.response.ProductResponse;
import com.web.dto.response.ProductSpecification;
import com.web.entity.*;
import com.web.exception.MessageException;
import com.web.mapper.ProductMapper;
import com.web.repository.*;
import com.web.servive.ProductService;
import com.web.utils.CharacterUtils;
import com.web.utils.CloudinaryService;
import com.web.utils.CommonPage;
import com.web.utils.FindByImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Repository
public class ProductServiceImp implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    @Autowired
    private ImportProductRepository importProductRepository;

    @Autowired
    private CommonPage commonPage;

    @Autowired
    EntityManager em;

    @Override
    public ProductResponse save(ProductRequest productRequest) {
        Product product = productMapper.productRequestToProduct(productRequest);
        if(product.getId() != null){
            throw new MessageException("id must null");
        }
        product.setCreatedDate(new Date(System.currentTimeMillis()));
        product.setCreatedTime(new Time(System.currentTimeMillis()));
        product.setQuantitySold(0);
        if (product.getAlias() == null) {
            product.setAlias(CharacterUtils.change(product.getName()));
        }
        if(product.getAlias() == ""){
            product.setAlias(CharacterUtils.change(product.getName()));
        }
        Product result = productRepository.save(product);

        for(Long id : productRequest.getListCategoryIds()){
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProduct(result);
            Category category = new Category();
            category.setId(id);
            productCategory.setCategory(category);
            productCategoryRepository.save(productCategory);
        }

        for(String link : productRequest.getLinkLinkImages()){
            ProductImage productImage = new ProductImage();
            productImage.setProduct(result);
            productImage.setLinkImage(link);
            productImageRepository.save(productImage);
        }

        for(ColorRequest color : productRequest.getColors()){
            ProductColor productColor = new ProductColor();
            productColor.setProduct(result);
            productColor.setColorName(color.getColorName());
            productColor.setLinkImage(color.getLinkImage());
            ProductColor colorResult = productColorRepository.save(productColor);
            for(SizeRequest size : color.getSize()){
                ProductSize productSize = new ProductSize();
                productSize.setProductColor(colorResult);
                productSize.setSizeName(size.getSizeName());
                productSize.setQuantity(size.getQuantity());
                productSizeRepository.save(productSize);
            }
        }
        ProductResponse response = productMapper.productToProResponse(productRepository.findById(result.getId()).get());
        return response;
    }

    @Override
    public ProductResponse update(ProductRequest productRequest) {
        Product product = productMapper.productRequestToProduct(productRequest);
        if(product.getId() == null){
            throw new MessageException("id product require");
        }
        Optional<Product> exist = productRepository.findById(product.getId());
        if(exist.isEmpty()){
            throw new MessageException("product not found");
        }

        product.setCreatedDate(exist.get().getCreatedDate());
        product.setCreatedTime(exist.get().getCreatedTime());
        product.setQuantitySold(exist.get().getQuantitySold());
        if (product.getAlias() == null) {
            product.setAlias(CharacterUtils.change(product.getName()));
        }
        if(product.getAlias() == ""){
            product.setAlias(CharacterUtils.change(product.getName()));
        }
        Product result = productRepository.save(product);

        productCategoryRepository.deleteByProduct(result.getId());
        for(Long id : productRequest.getListCategoryIds()){
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProduct(result);
            Category category = new Category();
            category.setId(id);
            productCategory.setCategory(category);
            productCategoryRepository.save(productCategory);
        }
        for(String link : productRequest.getLinkLinkImages()){
            ProductImage productImage = new ProductImage();
            productImage.setProduct(result);
            productImage.setLinkImage(link);
            productImageRepository.save(productImage);
        }
        for(ColorRequest color : productRequest.getColors()){
            ProductColor productColor = new ProductColor();
            productColor.setId(color.getId());
            productColor.setProduct(result);
            productColor.setColorName(color.getColorName());
            productColor.setLinkImage(color.getLinkImage());
            if(color.getLinkImage() == null){
                if(color.getId() != null){
                    Optional<ProductColor> ex = productColorRepository.findById(color.getId());
                    if(ex.isPresent()){
                        productColor.setLinkImage(ex.get().getLinkImage());
                    }
                }
            }
            ProductColor colorResult = productColorRepository.save(productColor);
            for(SizeRequest size : color.getSize()){
                ProductSize productSize = new ProductSize();
                productSize.setId(size.getId());
                productSize.setProductColor(colorResult);
                productSize.setSizeName(size.getSizeName());
                productSize.setQuantity(size.getQuantity());
                productSizeRepository.save(productSize);
            }
        }
        ProductResponse response = productMapper.productToProResponse(productRepository.findById(result.getId()).get());
        return response;
    }

    @Override
    public ProductResponse delete(Long idProduct) {
        Optional<Product> exist = productRepository.findById(idProduct);
        if(exist.isEmpty()){
            throw new MessageException("product not found");
        }
        if(invoiceDetailRepository.countByProduct(idProduct) > 0){
            productColorRepository.setNull(idProduct);
            productRepository.delete(exist.get());
        }
        else{
            importProductRepository.deleteByProduct(idProduct);
            productSizeRepository.deleteByProduct(idProduct);
            productRepository.delete(exist.get());
        }
        return null;
    }

    @Override
    public Page<ProductResponse> findAll(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductResponse> list = productMapper.listProductToProResponse(products.getContent());
        Page<ProductResponse> page = commonPage.restPage(products,list);
        return page;
    }

    @Override
    public List<ProductResponse> findAllList(String search) {
        if(search == null){
            search = "";
        }
        search = "%"+search+"%";
        List<Product> products = productRepository.findByParam(search);
        List<ProductResponse> list = productMapper.listProductToProResponse(products);
        return list;
    }

    @Override
    public Page<ProductResponse> search(String param, Pageable pageable) {
        Page<Product> products = productRepository.findAllByParam("%"+param+"%",pageable);
        List<ProductResponse> list = productMapper.listProductToProResponse(products.getContent());
        Page<ProductResponse> page = commonPage.restPage(products,list);
        return page;
    }

    @Override
    public Page<ProductResponse> findByCategory(Long idCategory, Pageable pageable) {
        Page<Product> products = productRepository.findByCategory(idCategory,pageable);
        List<ProductResponse> list = productMapper.listProductToProResponse(products.getContent());
        Page<ProductResponse> page = commonPage.restPage(products,list);
        return page;
    }

    @Override
    public Page<ProductResponse> searchFull(Double smallPrice, Double largePrice, List<Long> listIdCategory, Pageable pageable) {
        return null;
    }

//    @Override
//    public Page<ProductResponse> searchFullProduct(Double smallPrice, Double largePrice, List<Long> listIdCategory, Pageable pageable) {
//        if(smallPrice == null || largePrice == null){
//            smallPrice = 0D;
//            largePrice = 1000000000D;
//        }
//        String sql = "select p.* from Product p inner join product_category pc on pc.product_id = p.id" +
//                    " where (p.price >= ?1 and p.price <= ?2) ";
//        String sqlCount = "SELECT count(*) FROM (select count(*) from Product p inner join product_category pc on pc.product_id = p.id" +
//                " where (p.price >= ?1 and p.price <= ?2) ";
//        if(listIdCategory != null){
//            if(listIdCategory.size() > 0){
//                sql += " and (";
//                sqlCount += " and (";
//                for(int i=0; i<listIdCategory.size(); i++){
//                    var x=i+3;
//                    sql += " pc.category_id = ?"+x;
//                    sqlCount += " pc.category_id = ?"+x;
//                    if(i < listIdCategory.size() - 1){
//                        sql += " or";
//                        sqlCount += " or";
//                    }
//                }
//                sql += ") ";
//                sqlCount += ") ";
//            }
//        }
//        String sort = "";
//        if(pageable.getSort().isEmpty() == false){
//            sort = " order by ";
//            List<Sort.Order> list = pageable.getSort().get().toList();
//            for(int i=0; i<list.size(); i++){
//                Sort.Order p = list.get(i);
//                System.out.println("rotytty=="+p.getProperty()+", "+p.getDirection().name());
//                sort += "p."+p.getProperty() +" "+p.getDirection().name() ;
//                if(i<list.size()-1){
//                    sort+=", ";
//                }
//            }
//            System.out.println("sort by==="+sort);
//        }
//        sql += " group by p.id "+sort;
//        sqlCount += " group by p.id "+sort +" ) as id";
//
//        Query query = em.createNativeQuery(sql, Product.class);
//        Query queryCount = em.createNativeQuery(sqlCount);
//
//        query.setParameter(1,smallPrice);
//        query.setParameter(2,largePrice);
//
//        queryCount.setParameter(1,smallPrice);
//        queryCount.setParameter(2,largePrice);
//
//        if(listIdCategory != null){
//            if(listIdCategory.size() > 0){
//                for(int i=0; i<listIdCategory.size(); i++){
//                    var x=i+3;
//                    query.setParameter(x, listIdCategory.get(i));
//                    queryCount.setParameter(x, listIdCategory.get(i));
//                }
//            }
//        }
//        query.setMaxResults(pageable.getPageSize());
//        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
//        List<Product> list = query.getResultList();
//        System.out.println("sql count "+sqlCount);
//        BigInteger total = (BigInteger) queryCount.getSingleResult();
//        System.out.println("==========> total elm: "+total);
//        Page<Product> page = new PageImpl<>(list,pageable,total.longValue());
//        List<ProductResponse> productResponses = productMapper.listProductToProResponse(list);
//        return commonPage.restPage(page,productResponses);
//    }

    @Override
    public Page<ProductResponse> searchFullProduct(Double smallPrice, Double largePrice, List<Long> listIdCategory,List<Long> listTrademark, Pageable pageable) {
        if(smallPrice == null || largePrice == null){
            smallPrice = 0D;
            largePrice = 1000000000D;
        }
        ProductSpecification productSpecification = new ProductSpecification(listIdCategory, listTrademark, smallPrice, largePrice);
        Page<Product> page = productRepository.findAll(productSpecification, pageable);
        List<ProductResponse> productResponses = productMapper.listProductToProResponse(page.getContent());
        return commonPage.restPage(page,productResponses);
    }


    @Override
    public ProductResponse findByIdForAdmin(Long id) {
        Optional<Product> exist = productRepository.findById(id);
        if(exist.isEmpty()){
            throw new MessageException("product not found");
        }
        return productMapper.productToProResponse(exist.get());
    }

    @Override
    public ProductResponse findByIdForUser(Long id) {
        Optional<Product> exist = productRepository.findById(id);
        if(exist.isEmpty()){
            throw new MessageException("product not found");
        }
        return productMapper.productToProResponse(exist.get());
    }

    @Override
    public ProductResponse findByAlias(String alias) {
        Optional<Product> exist = productRepository.findByAlias(alias);
        if(exist.isEmpty()){
            throw new MessageException("product not found");
        }
        return productMapper.productToProResponse(exist.get());
    }


    @Autowired
    private FindByImage findByImage;

    @Autowired
    private CloudinaryService cloudinaryService;
    @Override
    public List<Product> findByImage(MultipartFile multipartFile) throws IOException {
        List<ProductImage> links = productImageRepository.findAll();
        List<Product> result = new ArrayList<>();
        List<Double> res = new ArrayList<>();
        File sr = cloudinaryService.convertMultiPartToFile(multipartFile);
        for(ProductImage p : links){
            res.add(findByImage.compare(p.getLinkImage(),sr));
        }
        res.forEach(p->{
            System.out.println("============> "+p);
        });
        return result;
    }

    @Override
    public List<ProductResponse> sanPhamBanChayThangNay() {
        List<Product> list = productRepository.sanPhamBanChayThangNay();
        List<ProductResponse> productResponses = productMapper.listProductToProResponse(list);
        return productResponses;
    }
}
