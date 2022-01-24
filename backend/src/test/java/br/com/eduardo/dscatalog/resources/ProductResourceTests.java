package br.com.eduardo.dscatalog.resources;

import br.com.eduardo.dscatalog.dto.CategoryDTO;
import br.com.eduardo.dscatalog.dto.ProductDTO;
import br.com.eduardo.dscatalog.entities.Category;
import br.com.eduardo.dscatalog.entities.Product;
import br.com.eduardo.dscatalog.repositories.CategoryRepository;
import br.com.eduardo.dscatalog.repositories.ProductRepository;
import br.com.eduardo.dscatalog.services.ProductService;
import br.com.eduardo.dscatalog.services.exceptions.DatabaseException;
import br.com.eduardo.dscatalog.services.exceptions.ResourceNotFoundException;
import br.com.eduardo.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService service;
    private CategoryRepository categoryRepository;
    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;
    private long existingId;
    private long nonExistingId;

    @Autowired
    private ObjectMapper mapper;
    private long dependentId;

    @BeforeEach
    void setup() throws Exception {
        existingId = 1L;

        nonExistingId = 2L;
        dependentId = 3L;
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));

        when(service.findAllPaged(any())).thenReturn(page);
        //Simulando comportamento do método findById do Service
        when(service.findById(existingId)).thenReturn(productDTO);
        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
        //Simulando comportamento do método update do Service
        when(service.update(eq(existingId), any())).thenReturn(productDTO);
        when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        when(service.insert(any())).thenReturn(productDTO);


        //Simulando comportamento do método delete do Service
        doNothing().when(service).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
        doThrow(DatabaseException.class).when(service).delete(dependentId);
    }

    @Test
    public void insertShouldReturnStatus201AndProductDTO() throws Exception {
        String jsonBody = mapper.writeValueAsString(productDTO);

        ResultActions result = mvc.perform(post("/products")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());

    }

    @Test
    public void deleteShouldNoContentWhenIdExists() throws Exception {
        ResultActions result = mvc.perform(delete("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }
    @Test
    public void deleteShouldReturnNotFoundOWhenIdDoesNotExist() throws Exception{
        ResultActions result = mvc.perform(delete("/products/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }



    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception{
        String jsonBody = mapper.writeValueAsString(productDTO);

        ResultActions result = mvc.perform(put("/products/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void updateShouldThrowsNotFoundExceptionDTOWhenIdDoesNotExist() throws Exception{
        String jsonBody = mapper.writeValueAsString(productDTO);

        ResultActions result = mvc.perform(put("/products/{id}", nonExistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        ResultActions result = mvc.perform(get("/products")
            .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnsProductDTOWhenIdExists() throws Exception {
        ResultActions result = mvc.perform(get("/products/{id}", existingId));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
    }
    @Test
    public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {
        ResultActions result = mvc.perform(get("/products/{id}", nonExistingId));
        result.andExpect(status().isNotFound());
    }

}

