package org.wayster.com.emprestimos.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wayster.com.emprestimos.Entity.UsuarioEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByUsername(String username);
}
