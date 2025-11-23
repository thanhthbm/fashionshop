package com.thanhthbm.fashionshop.controller;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.dto.Format.ApiResponse;
import com.thanhthbm.fashionshop.dto.Cart.CartItem;
import com.thanhthbm.fashionshop.service.CartService;
import jakarta.validation.Valid; 
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<CartItem>>> getCart(@AuthenticationPrincipal User user) {
    if (user == null) {

      return ResponseEntity.status(401).build();
    }
    return ResponseEntity.ok(ApiResponse.success(cartService.getCart(user.getId())));
  }

  @PostMapping("/add")
  public ResponseEntity<ApiResponse<List<CartItem>>> addToCart(
      @AuthenticationPrincipal User user,
      @RequestBody @Valid CartItem item
  ) {
    List<CartItem> updatedCart = cartService.addToCart(user.getId(), item);
    return ResponseEntity.ok(ApiResponse.success(updatedCart));
  }

  @PutMapping("/update/{variantId}")
  public ResponseEntity<ApiResponse<List<CartItem>>> updateQuantity(
      @AuthenticationPrincipal User user,
      @PathVariable UUID variantId,
      @RequestParam Integer quantity
  ) {
    List<CartItem> cartItems = cartService.updateQuantity(user.getId(), variantId, quantity);
    return ResponseEntity.ok(ApiResponse.success(cartItems));
  }

  @DeleteMapping("/{variantId}")
  public ResponseEntity<ApiResponse<List<CartItem>>> removeFromCart(
      @AuthenticationPrincipal User user,
      @PathVariable UUID variantId
  ) {
    List<CartItem> updatedCart = cartService.removeFromCart(user.getId(), variantId);
    return ResponseEntity.ok(ApiResponse.success(updatedCart));
  }

  @DeleteMapping("/clear")
  public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal User user) {
    cartService.clearCart(user.getId());
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}