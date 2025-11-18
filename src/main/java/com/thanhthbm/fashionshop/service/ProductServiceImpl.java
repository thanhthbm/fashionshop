package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.dto.ProductDTO;
import com.thanhthbm.fashionshop.dto.ProductRequest;
import com.thanhthbm.fashionshop.dto.ResultPaginationDTO;
import com.thanhthbm.fashionshop.dto.ResultPaginationDTO.Meta;
import com.thanhthbm.fashionshop.entity.Product;
import com.thanhthbm.fashionshop.exception.ResourceNotFoundException;
import com.thanhthbm.fashionshop.mapper.ProductMapper;
import com.thanhthbm.fashionshop.repository.ProductRepository;
import com.thanhthbm.fashionshop.specification.ProductSpecification;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private CategoryService categoryService;

  @Autowired
  private ProductMapper productMapper;

  @Override
  public Product addProduct(ProductDTO productDTO) {
    Product product = productMapper.mapToProductEntity(productDTO);
    return productRepository.save(product);
  }

  @Override
  public ResultPaginationDTO getAllProducts(Specification<Product> spec, Pageable pageable) {

    Page<Product> products = productRepository.findAll(spec, pageable);
    List<ProductDTO> productDTOS = productMapper.getProductDTOs(products.getContent());

    ResultPaginationDTO resultPaginationDTO = ResultPaginationDTO.builder()
        .meta(
            Meta.builder()
                .page(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .pages(products.getTotalPages())
                .total(products.getTotalElements())
            .build()
        )
        .result(productDTOS)
        .build();
    return resultPaginationDTO;
  }

  @Override
  public ProductDTO getProductBySlug(String slug, Pageable pageable) {
    Product product = productRepository.findBySlug(slug);

    if (null == product) {
      throw new ResourceNotFoundException("Product not found");
    }

    ProductDTO productDTO = productMapper.mapProductToDTO(product);
    productDTO.setCategoryId(product.getCategory().getId());
    productDTO.setCategoryTypeId(product.getCategoryType().getId());
    productDTO.setVariants(productMapper.mapProductVariantListToDTO(product.getProductVariants()));
    productDTO.setProductResources(productMapper.mapProductResourcesListToDTO(product.getResources()));

    return productDTO;
  }

  @Override
  public ProductDTO getProductById(UUID id) {
    Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    if (null == product) {
      throw new ResourceNotFoundException("Product not found");
    }



    ProductDTO productDTO = productMapper.mapProductToDTO(product);
    productDTO.setCategoryId(product.getCategory().getId());
    productDTO.setCategoryTypeId(product.getCategoryType().getId());
    productDTO.setCategoryName(product.getCategory().getName());
    productDTO.setCategoryTypeName(product.getCategoryType().getName());
    productDTO.setVariants(productMapper.mapProductVariantListToDTO(product.getProductVariants()));
    productDTO.setProductResources(productMapper.mapProductResourcesListToDTO(product.getResources()));

    return productDTO;
  }

  @Override
  public Product updateProduct(ProductDTO productDTO) {
    Product product = productRepository.findById(productDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    if (null == product) {
      throw new ResourceNotFoundException("Product not found");
    }

    return productRepository.save(productMapper.mapToProductEntity(productDTO));
  }

  @Override
  public Product fetchProductById(UUID id) {
    return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
  }

  @Override
  public List<ProductDTO> getAllNewProducts(Boolean isNewArrival) {
    List<Product> product =  this.productRepository.findByIsNewArrival(true);

    List<ProductDTO> productDTOS = productMapper.getProductDTOs(product);
    return productDTOS;

  }

}
