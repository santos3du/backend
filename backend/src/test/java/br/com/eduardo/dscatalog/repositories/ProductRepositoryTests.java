package br.com.eduardo.dscatalog.repositories;

import br.com.eduardo.dscatalog.entities.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.Instant;
import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {
    @Autowired
    private ProductRepository productRepository;

    private long existingId;
    private long nonExistingId;
    private int countTotalProducts;

    @BeforeEach
    void setup() throws Exception {
        existingId = 1L;
        nonExistingId = 30L;
        countTotalProducts = 25;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        productRepository.deleteById(existingId);
        Optional<Product> product = productRepository.findById(existingId);
        Assertions.assertFalse(product.isPresent());
    }

    @Test
    public void deleteShouldThrowsEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(EmptyResultDataAccessException.class,
        ()-> {
            productRepository.deleteById(nonExistingId);
        });
    }

    @Test
    public void saveShouldThrowsExceptionWhenIdIsNull(){
        Product p = new Product(null, "Mp3 player", "Mp3 player com bluetoohh integrado", 189.0,
         "https://decathlonpro.vteximg.com.br/arquivos/ids/316126-1000-1000/onsound-110-unique1.jpg?v=636553010416600000"
        ,Instant.now());
        p.setId(null);
        productRepository.save(p);

        Assertions.assertNotNull(p.getId());
        Assertions.assertEquals(countTotalProducts + 1, p.getId());

    }

    @Test
    public void findByIdShouldReturnsIdWhenIdExists(){
        Product p = new Product();
        p.setId(22L);
        Optional<Product> optional = productRepository.findById(p.getId());

        Assertions.assertTrue(optional.isPresent());
        Assertions.assertTrue(p.getId() == optional.get().getId());
    }

    @Test
    public void findByIdShouldReturnsEmptyOptionalWhenIdNotExists() {
        Product p = new Product();
        p.setId(27L);
        Optional<Product> optional = productRepository.findById(27L);
        System.out.println("Valor do optional: " + optional);

        Assertions.assertFalse(optional.isPresent());
        Assertions.assertTrue(!optional.isPresent());
    }



}
