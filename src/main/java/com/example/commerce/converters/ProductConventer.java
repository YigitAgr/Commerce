package com.example.commerce.converters;
import com.example.commerce.dto.ProductDto;
import com.example.commerce.model.Product;
import org.springframework.stereotype.Component;

@Component

public class ProductConventer {

    // Convert Product entity to ProductDto
    public ProductDto convertProductToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        productDto.setStock(product.getStock());
        return productDto;
    }

    // Convert ProductDto to Product entity
    public Product convertDtoToProduct(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        return product;
    }


}
