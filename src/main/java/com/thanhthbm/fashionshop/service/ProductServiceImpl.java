package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.dto.ProductDTO;
import com.thanhthbm.fashionshop.entity.Product;
import com.thanhthbm.fashionshop.exception.ResourceNotFoundException;
import com.thanhthbm.fashionshop.mapper.ProductMapper;
import com.thanhthbm.fashionshop.repository.ProductRepository;
import com.thanhthbm.fashionshop.specification.ProductSpecification;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
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
  public List<ProductDTO> getAllProducts(UUID categoryId, UUID categoryTypeId) {
    Specification<Product> productSpecification = Specification.allOf();

    if (null != categoryId) {
      productSpecification = productSpecification.and(ProductSpecification.hasCategoryId(categoryId));
    }
    if (null != categoryTypeId) {
      productSpecification = productSpecification.and(ProductSpecification.hasCategoryTypeId(categoryTypeId));
    }

    List<Product> products = productRepository.findAll(productSpecification);
    List<ProductDTO> productDTOS = productMapper.getProductDTOs(products);
    return productDTOS;
  }

  @Override
  public ProductDTO getProductBySlug(String slug) {
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


}
