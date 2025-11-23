package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.dto.Product.ProductDTO;
import com.thanhthbm.fashionshop.entity.Product;
import com.thanhthbm.fashionshop.entity.WishList;
import com.thanhthbm.fashionshop.exception.ResourceNotFoundException;
import com.thanhthbm.fashionshop.mapper.ProductMapper;
import com.thanhthbm.fashionshop.repository.ProductRepository;
import com.thanhthbm.fashionshop.repository.WishListRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishListService {
  private final WishListRepository wishListRepository;
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  public String toggleWishlist(User user, UUID productId) {
    if (wishListRepository.existsByUserIdAndProductId(user.getId(), productId)) {
      wishListRepository.deleteByUserIdAndProductId(user.getId(), productId);
      return "Removed from wishlist";
    } else {
      Product product = productRepository.findById(productId)
          .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

      WishList wishlist = WishList.builder()
          .user(user)
          .product(product)
          .build();
      wishListRepository.save(wishlist);
      return "Added to wishlist";
    }
  }

  public List<ProductDTO> getMyWishlist(User user) {
    List<WishList> wishlists = wishListRepository.findByUserId(user.getId());

    return wishlists.stream()
        .map(w -> productMapper.mapProductToDTO(w.getProduct()))
        .collect(Collectors.toList());
  }
}
