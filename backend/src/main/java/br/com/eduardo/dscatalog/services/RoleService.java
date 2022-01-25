package br.com.eduardo.dscatalog.services;

import br.com.eduardo.dscatalog.dto.RoleDTO;
import br.com.eduardo.dscatalog.entities.Role;
import br.com.eduardo.dscatalog.repositories.RoleRepository;
import br.com.eduardo.dscatalog.services.exceptions.DatabaseException;
import br.com.eduardo.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class RoleService {
    private RoleRepository repo;

    @Autowired
    public RoleService(RoleRepository repo){
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Page<RoleDTO> findAllPaged(Pageable pageable){
        Page<Role> list = repo.findAll(pageable);
        return list.map(x -> new RoleDTO(x));
    }

    @Transactional(readOnly = true)
    public RoleDTO findByid(Long id) {
        Optional<Role> obj = repo.findById(id);
        Role entity = obj.orElseThrow(() -> new ResourceNotFoundException("Id not found."));
        return new RoleDTO(entity);
    }

    @Transactional
    public RoleDTO insert(RoleDTO dto) {
        Role entity = new Role();
        entity.setAuthority(dto.getAuthority());
        entity = repo.save(entity);
        return new RoleDTO(entity);

    }

    @Transactional
    public RoleDTO update(Long id, RoleDTO dto) {
        try {
            Role entity = repo.getOne(id);
            entity.setAuthority(dto.getAuthority());
            entity = repo.save(entity);
            return new RoleDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id );
        }
    }


    public void delete(Long id) {
        try {
            repo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found. " + id);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity violation");
        }
    }
}
