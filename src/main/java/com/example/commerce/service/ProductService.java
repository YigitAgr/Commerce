package com.example.commerce.service;

import com.example.commerce.converters.ProductConventer;
import com.example.commerce.dto.ProductDto;
import com.example.commerce.model.Product;
import com.example.commerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    private ProductConventer productConventer;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Add a new product
    public ProductDto addProduct(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        productRepository.save(product);

        // Return the saved product as a DTO
        return productConventer.convertProductToDto(product);
    }

    // Get a product by ID
    public ProductDto getProduct(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            return productConventer.convertProductToDto(product.get());
        }
        throw new RuntimeException("Product not found with id " + productId);
    }

    // Get all products
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(productConventer::convertProductToDto)
                .toList();
    }

    // Update an existing product
    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));

        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());

        productRepository.save(product);

        return productConventer.convertProductToDto(product);
    }

    // Delete a product by ID
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));

        productRepository.delete(product);
    }

}
