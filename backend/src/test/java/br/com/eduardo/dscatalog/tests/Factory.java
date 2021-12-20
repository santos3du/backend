package br.com.eduardo.dscatalog.tests;

import br.com.eduardo.dscatalog.dto.ProductDTO;
import br.com.eduardo.dscatalog.entities.Category;
import br.com.eduardo.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(1L, "Phone", "Good Phone, amazing", 800.0, "https://img.com/phone.png", Instant.now());
        product.getCategories().add(createCategory());
        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());
    }

    public static Category createCategory() {
        return new Category(1L, "Electronics");
    }

}
