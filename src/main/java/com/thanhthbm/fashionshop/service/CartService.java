package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.dto.Cart.CartItem;
import com.thanhthbm.fashionshop.repository.ProductVariantRepository; // Nên dùng Repo trực tiếp để check lock
import com.thanhthbm.fashionshop.entity.ProductVariant;
import com.thanhthbm.fashionshop.exception.ResourceNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {
  private final RedisTemplate<String, Object> redisTemplate;
  private final ProductVariantRepository productVariantRepository; // Sửa: Dùng Repo để lấy entity đầy đủ
  private static final String CART_PREFIX = "cart:";

  private String getCartKey(UUID userId){
    return CART_PREFIX + userId.toString();
  }

  public List<CartItem> addToCart(UUID userId, CartItem item){
    String key = getCartKey(userId);
    HashOperations<String, String, CartItem> hashOperations = redisTemplate.opsForHash();
    String variantIdStr = item.getVariantId().toString();

    ProductVariant variant = productVariantRepository.findById(item.getVariantId())
        .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

    Integer currentStock = variant.getStockQuantity();
    Integer quantityToBuy = item.getQuantity();

    if (hashOperations.hasKey(key, variantIdStr)){
      CartItem existingCartItem = hashOperations.get(key, variantIdStr);
      quantityToBuy += existingCartItem.getQuantity(); // Tổng định mua = Mới + Cũ

      item.setQuantity(quantityToBuy);
    }

    if (quantityToBuy > currentStock){
      throw new IllegalArgumentException("Kho chỉ còn " + currentStock + " sản phẩm. Bạn không thể thêm " + quantityToBuy);
    }

    hashOperations.put(key, variantIdStr, item);

    return getCart(userId);
  }

  public List<CartItem> getCart(UUID userId){
    String key = getCartKey(userId);
    Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
    return entries.values().stream()
        .map(obj -> (CartItem) obj)
        .sorted(Comparator.comparing(CartItem::getProductName))
        .collect(Collectors.toList());
  }

  public List<CartItem> removeFromCart(UUID userId, UUID variantId){
    redisTemplate.opsForHash().delete(getCartKey(userId), variantId.toString());
    return getCart(userId);
  }

  public List<CartItem> updateQuantity(UUID userId, UUID variantId, Integer quantity){
    String key = getCartKey(userId);
    HashOperations<String, String, CartItem> hashOperations = redisTemplate.opsForHash();
    String variantIdStr = variantId.toString();

    if (quantity <= 0) {
      throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
    }

    if (hashOperations.hasKey(key, variantIdStr)){
      ProductVariant variant = productVariantRepository.findById(variantId)
          .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

      if (quantity > variant.getStockQuantity()){
        throw new IllegalArgumentException("Kho chỉ còn " + variant.getStockQuantity() + " sản phẩm.");
      }

      CartItem existingCartItem = hashOperations.get(key, variantIdStr);
      existingCartItem.setQuantity(quantity);
      hashOperations.put(key, variantIdStr, existingCartItem);
    }

    return getCart(userId);
  }

  public void clearCart(UUID userId){
    String key = getCartKey(userId);
    redisTemplate.delete(key);
  }
}