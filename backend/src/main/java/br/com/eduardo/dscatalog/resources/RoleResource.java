package br.com.eduardo.dscatalog.resources;

import br.com.eduardo.dscatalog.dto.RoleDTO;
import br.com.eduardo.dscatalog.dto.UserDTO;
import br.com.eduardo.dscatalog.services.RoleService;
import br.com.eduardo.dscatalog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/roles")
public class RoleResource {
    @Autowired
    private RoleService service;

    @GetMapping
    public ResponseEntity<Page<RoleDTO>> findAll(Pageable pageable){
        Page<RoleDTO> list = service.findAllPaged(pageable);
        return  ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RoleDTO> findById(@PathVariable Long id){
        RoleDTO dto = service.findByid(id);
        return  ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<RoleDTO> insert(@RequestBody RoleDTO dto){
        dto = service.insert(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable Long id, @RequestBody RoleDTO dto){
        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
