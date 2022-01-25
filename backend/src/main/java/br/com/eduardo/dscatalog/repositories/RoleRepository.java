package br.com.eduardo.dscatalog.repositories;

import br.com.eduardo.dscatalog.entities.Role;
import br.com.eduardo.dscatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
