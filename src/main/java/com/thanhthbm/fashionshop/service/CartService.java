package com.thanhthbm.fashionshop.service;

import com.thanhthbm.fashionshop.dto.CartItem;
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
  private static final String CART_PREFIX = "cart:";

  private String getCartKey(UUID userId){
    return CART_PREFIX + userId.toString();
  }

  public void addToCart(UUID userId, CartItem item){
    String key = getCartKey(userId);
    HashOperations<String, String, CartItem> hashOperations = redisTemplate.opsForHash();

    if (hashOperations.hasKey(key, item.getVariantId().toString())){
      CartItem existingCartItem = hashOperations.get(key, item.getVariantId().toString());
      existingCartItem.setQuantity(existingCartItem.getQuantity() + item.getQuantity());
      hashOperations.put(key, item.getVariantId().toString(), existingCartItem);
    } else {
      hashOperations.put(key, item.getVariantId().toString(), item);
    }
  }

  public List<CartItem> getCart(UUID userId){
    String key = getCartKey(userId);
    Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

    return entries.values().stream().map(obj -> (CartItem) obj).collect(Collectors.toList());
  }

  public void removeFromCart(UUID userId, UUID variantId){
    redisTemplate.opsForHash().delete(getCartKey(userId), variantId.toString());
  }

  public void updateQuantity(UUID userId, UUID variantId, Integer quantity){
    String key = getCartKey(userId);
    HashOperations<String, String, CartItem> hashOperations = redisTemplate.opsForHash();

    if (hashOperations.hasKey(key, variantId.toString())){
      CartItem existingCartItem = hashOperations.get(key, variantId.toString());
      existingCartItem.setQuantity(quantity);
      hashOperations.put(key, variantId.toString(), existingCartItem);
    }
  }

  public void clearCart(UUID userId){
    String key = getCartKey(userId);
    redisTemplate.delete(key);
  }
}
