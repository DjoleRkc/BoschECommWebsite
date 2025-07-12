package my.ecommerce.backend.repository;

import my.ecommerce.backend.models.CartItem;
import my.ecommerce.backend.models.Product;
import my.ecommerce.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepo extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserAndProduct(User user, Product product);

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    Optional<CartItem> findByIdAndUser(Long id, User user);

    Optional<CartItem> findByIdAndUserId(Long id, Long userId);

    void deleteByUser(User user);

    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci WHERE ci.user.id = :userId")
    Integer getTotalItemsInCart(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(ci.quantity * ci.product.price), 0) FROM CartItem ci WHERE ci.user.id = :userId")
    Double getCartTotalValue(@Param("userId") Long userId);
}
