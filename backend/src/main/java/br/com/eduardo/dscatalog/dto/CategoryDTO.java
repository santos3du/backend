package br.com.eduardo.dscatalog.dto;

import br.com.eduardo.dscatalog.entities.Category;

import java.io.Serializable;

public class CategoryDTO implements Serializable {
    public static final long serialVersionUID = 1L;
    private Long id;
    private String name;

    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryDTO(Category entity){
        this.id = entity.getId();
        this.name = entity.getName();
    }

    public CategoryDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
