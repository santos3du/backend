package br.com.eduardo.dscatalog.services;

import br.com.eduardo.dscatalog.dto.RoleDTO;
import br.com.eduardo.dscatalog.dto.UserDTO;
import br.com.eduardo.dscatalog.dto.UserInsertDTO;
import br.com.eduardo.dscatalog.dto.UserUpdateDTO;
import br.com.eduardo.dscatalog.entities.Role;
import br.com.eduardo.dscatalog.entities.User;
import br.com.eduardo.dscatalog.repositories.RoleRepository;
import br.com.eduardo.dscatalog.repositories.UserRepository;
import br.com.eduardo.dscatalog.services.exceptions.DatabaseException;
import br.com.eduardo.dscatalog.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repo;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repo, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder){
        this.repo = repo;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable){
        Page<User> list = repo.findAll(pageable);
        return list.map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO findByid(Long id) {
        Optional<User> obj = repo.findById(id);
        User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Id not found."));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity = repo.save(entity);
        return new UserDTO(entity);

    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        try {
            User entity = repo.getOne(id);
            copyDtoToEntity(dto, entity);
            entity = repo.save(entity);
            return new UserDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id );
        }
    }

    private void copyDtoToEntity(UserDTO dto, User entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());

        entity.getRoles().clear();
        for(RoleDTO roleDto : dto.getRoles()) {
            Role role = roleRepository.getOne(roleDto.getId());
            entity.getRoles().add(role);
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


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByEmail(username);
        logger.info(user.getEmail());
        if(user == null){
            logger.error("User not found: "+ username);
            throw new UsernameNotFoundException("Email not found");
        }
        logger.info("User get log in: " + user.getEmail());
        return  user;

    }
}
