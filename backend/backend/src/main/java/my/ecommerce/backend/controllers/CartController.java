package my.ecommerce.backend.controllers;

import my.ecommerce.backend.dto.AddToCartRequest;
import my.ecommerce.backend.dto.CartResponse;
import my.ecommerce.backend.dto.UpdateCartItemRequest;
import my.ecommerce.backend.models.CartItem;
import my.ecommerce.backend.services.CartService;
import my.ecommerce.backend.utility.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Shopping cart management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartItem> addToCart(@Valid @RequestBody AddToCartRequest request,
                                              HttpServletRequest httpRequest) {
        try {
            Long userId = extractUserIdFromRequest(httpRequest);
            CartItem cartItem = cartService.addToCart(userId, request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(cartItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get cart contents")
    public ResponseEntity<CartResponse> getCart(HttpServletRequest httpRequest) {
        try {
            Long userId = extractUserIdFromRequest(httpRequest);
            CartResponse cartResponse = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(cartResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/item/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateCartItemRequest request,
                                                   HttpServletRequest httpRequest) {
        try {
            Long userId = extractUserIdFromRequest(httpRequest);
            CartItem updatedItem = cartService.updateCartItem(userId, id, request.getQuantity());
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/item/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            Long userId = extractUserIdFromRequest(httpRequest);
            boolean removed = cartService.removeFromCart(userId, id);
            return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractUserId(token);
        }
        throw new RuntimeException("No valid JWT token found");
    }
}
