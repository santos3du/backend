package br.com.eduardo.dscatalog.services;

import br.com.eduardo.dscatalog.dto.CategoryDTO;
import br.com.eduardo.dscatalog.dto.ProductDTO;
import br.com.eduardo.dscatalog.entities.Category;
import br.com.eduardo.dscatalog.entities.Product;
import br.com.eduardo.dscatalog.repositories.CategoryRepository;
import br.com.eduardo.dscatalog.repositories.ProductRepository;
import br.com.eduardo.dscatalog.services.exceptions.DatabaseException;
import br.com.eduardo.dscatalog.services.exceptions.ResourceNotFoundException;
import br.com.eduardo.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;
    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependendedId;
    private PageImpl page;
    private Product product;
    private ProductDTO productDTO;
    private CategoryDTO categoryDTO;
    private Category category;

    @BeforeEach
    void setup() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        dependendedId = 4L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));
        productDTO = Factory.createProductDTO();
        category = Factory.createCategory();
        /**
         * Esses comportamentos são simulações para não ter que carregar todo o contexto da aplicação
         * por isso usamos a classe e metodos do Mockito para simular o comportamento do repository
         */

        //Comportamentos para demais metodos do repository
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.getOne(existingId)).thenReturn(product);
        Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependendedId);
    }

    @Test
    public void updateShouldResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class,
            () -> {
                ProductDTO dto = new ProductDTO();
                dto.setId(nonExistingId);
                service.update(nonExistingId, dto);
            });
    }

    @Test
    public void updateShouldReturntProductDTOWhenIdExists(){
        ProductDTO dto = Factory.createProductDTO();

        ProductDTO resultDto = service.update(existingId, dto);

        Assertions.assertNotNull(resultDto);
    }

    @Test
    public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class,
            () -> {
                ProductDTO dto = service.findById(nonExistingId);
        });
    }

    @Test
    public void findByIdShouldReturnsProductDTOWhenIdExists(){
        ProductDTO dto = service.findById(existingId);

        Assertions.assertNotNull(dto);
    }

    @Test
    public void findAllPagedShouldReturnPage(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).findAll(pageable);
    }

    @Test
    public void deleteShouldThrowsDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class,
                () -> {
                    service.delete(dependendedId);
                });

        Mockito.verify(repository, Mockito.times(1)).deleteById(dependendedId);
    }


    @Test
    public void deleteShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> {
            service.delete(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }
}
