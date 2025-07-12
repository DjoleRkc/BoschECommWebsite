package my.ecommerce.backend.services;

import my.ecommerce.backend.models.Product;
import my.ecommerce.backend.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepo productRepository;

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }




    public Page<Product> searchProductsByName(String name, Pageable pageable) {
        return productRepository.findByName(name, pageable);
    }


    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setDescription(productDetails.getDescription());
                    product.setShortDescription(productDetails.getShortDescription());
                    product.setPrice(productDetails.getPrice());
                    product.setStockQuantity(productDetails.getStockQuantity());
                    return productRepository.save(product);
                });
    }


    public void updateStock(Long productId, Integer quantity) {
        productRepository.findById(productId)
                .ifPresent(product -> {
                    product.setStockQuantity(product.getStockQuantity() - quantity);
                    productRepository.save(product);
                });
    }
}
