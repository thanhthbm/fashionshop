package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.dto.Format.ApiResponse;
import com.thanhthbm.fashionshop.dto.Product.ProductDTO;
import com.thanhthbm.fashionshop.service.WishListService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishListController {
  private final WishListService wishListService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<ProductDTO>>> getWishList(@AuthenticationPrincipal User user){
    return ResponseEntity.ok(ApiResponse.success(wishListService.getMyWishlist(user)));
  }

  @PostMapping("/toggle/{productId}")
  public ResponseEntity<ApiResponse<String>> toggle(@AuthenticationPrincipal User user, @PathVariable UUID productId) {
    String message = wishListService.toggleWishlist(user, productId);
    return ResponseEntity.ok(ApiResponse.success(message, message));
  }
}
