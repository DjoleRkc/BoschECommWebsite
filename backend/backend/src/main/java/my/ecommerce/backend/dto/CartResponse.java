package my.ecommerce.backend.dto;

import my.ecommerce.backend.models.CartItem;
import java.math.BigDecimal;
import java.util.List;

public class CartResponse {

    private List<CartItem> items;
    private Integer totalItems;
    private BigDecimal totalPrice;

    public CartResponse() {}

    public CartResponse(List<CartItem> items, Integer totalItems, BigDecimal totalPrice) {
        this.items = items;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
    }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
}



