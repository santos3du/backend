package br.com.eduardo.dscatalog.services;

import br.com.eduardo.dscatalog.dto.CategoryDTO;
import br.com.eduardo.dscatalog.entities.Category;
import br.com.eduardo.dscatalog.repositories.CategoryRepository;
import br.com.eduardo.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private CategoryRepository repo;

    @Autowired
    public CategoryService(CategoryRepository repo){
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll(){
        List<Category> list = repo.findAll();
        return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO findByid(Long id) {
        Optional<Category> obj = repo.findById(id);
        Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Id not found."));
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category entity = new Category();
        entity.setName(dto.getName());
        entity = repo.save(entity);
        return new CategoryDTO(entity);

    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category entity = repo.getOne(id);
            entity.setName(dto.getName());
            entity = repo.save(entity);
            return new CategoryDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id );
        }
    }
}
