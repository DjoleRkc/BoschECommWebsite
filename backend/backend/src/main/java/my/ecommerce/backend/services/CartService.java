package my.ecommerce.backend.services;

import my.ecommerce.backend.dto.CartResponse;
import my.ecommerce.backend.models.CartItem;
import my.ecommerce.backend.models.Product;
import my.ecommerce.backend.models.User;
import my.ecommerce.backend.repository.CartItemRepo;
import my.ecommerce.backend.repository.ProductRepo;
import my.ecommerce.backend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartItemRepo cartItemRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ProductRepo productRepository;

    public CartItem addToCart(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        Optional<CartItem> existingItem = cartItemRepository.findByUserAndProduct(user, product);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;

            if (product.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Insufficient stock");
            }

            cartItem.setQuantity(newQuantity);
            return cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem(user, product, quantity);
            return cartItemRepository.save(cartItem);
        }
    }

    public CartResponse getCartByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        int totalItems = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cartItems, totalItems, totalPrice);
    }

    public CartItem updateCartItem(Long userId, Long cartItemId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartItem cartItem = cartItemRepository.findByIdAndUser(cartItemId, user)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Product product = cartItem.getProduct();

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    public boolean removeFromCart(Long userId, Long cartItemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<CartItem> cartItem = cartItemRepository.findByIdAndUser(cartItemId, user);

        if (cartItem.isPresent()) {
            cartItemRepository.delete(cartItem.get());
            return true;
        }
        return false;
    }

    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartItemRepository.deleteByUser(user);
    }

    public Integer getCartItemCount(Long userId) {
        return cartItemRepository.getTotalItemsInCart(userId);
    }

    public Double getCartTotalValue(Long userId) {
        return cartItemRepository.getCartTotalValue(userId);
    }
}