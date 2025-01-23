package org.wayster.com.emprestimos.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wayster.com.emprestimos.Entity.ClientesEntity;

@Repository
public interface ClientesRepository extends JpaRepository<ClientesEntity, Long> {



}
