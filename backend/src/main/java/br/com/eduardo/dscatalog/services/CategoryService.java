package br.com.eduardo.dscatalog.services;

import br.com.eduardo.dscatalog.entities.Category;
import br.com.eduardo.dscatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private CategoryRepository repo;

    @Autowired
    public CategoryService(CategoryRepository repo){
        this.repo = repo;
    }

    public List<Category> findAll(){
        return repo.findAll();
    }
}
