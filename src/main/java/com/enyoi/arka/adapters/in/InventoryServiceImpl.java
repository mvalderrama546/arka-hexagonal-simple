package com.enyoi.arka.adapters.in;

import com.enyoi.arka.domain.entities.Product;
import com.enyoi.arka.domain.entities.ProductCategory;
import com.enyoi.arka.domain.exception.ProductNotFoundException;
import com.enyoi.arka.domain.ports.in.InventoryService;
import com.enyoi.arka.domain.ports.out.NotificationService;
import com.enyoi.arka.domain.ports.out.ProductRepository;
import com.enyoi.arka.domain.valueobjects.Money;
import com.enyoi.arka.domain.valueobjects.ProductId;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class InventoryServiceImpl implements InventoryService {
    
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public InventoryServiceImpl(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = Objects.requireNonNull(productRepository);
        this.notificationService = Objects.requireNonNull(notificationService);
    }

    @Override
    public Product registerProduct(String name, String description, Money price, int stock, String category) {
        ProductId productId = ProductId.of(UUID.randomUUID().toString());
        ProductCategory productCategory = ProductCategory.valueOf(category);
        
        Product product = Product.builder()
                .id(productId)
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .category(productCategory)
                .build();
        
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(ProductId id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id.value()));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateStock(ProductId id, int newStock) {
        Product product = getProductById(id);
        Product updatedProduct = Product.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(newStock)
                .category(product.getCategory())
                .build();
        return productRepository.save(updatedProduct);
    }

    @Override
    public void reduceStock(ProductId id, int quantity) {
        Product product = getProductById(id);
        product.reduceStock(quantity);
        productRepository.save(product);
    }

    @Override
    public List<Product> getLowStockProducts() {

        return productRepository.findLowStockProducts(10);

    }

    @Override
    public void generateRestockReport() {
        List<Product> lowStockProducts = productRepository.findLowStockProducts(10);
        for (Product product : lowStockProducts) {
            notificationService.notifyLowStockAlert(product.getName(), product.getStock());
        }
    }
}
