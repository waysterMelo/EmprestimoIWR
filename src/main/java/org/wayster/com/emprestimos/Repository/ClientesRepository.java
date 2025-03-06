package org.wayster.com.emprestimos.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.wayster.com.emprestimos.Dto.EmprestimoDto;
import org.wayster.com.emprestimos.Entity.ClientesEntity;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientesRepository extends JpaRepository<ClientesEntity, Long> {

    Optional<ClientesEntity> findByCpf(String cpf);

    List<ClientesEntity> findAll();

    @Query("select c from ClientesEntity c where LOWER(c.nome) LIKE  lower(concat('%', :nome, '%') ) ")
    List<ClientesEntity> findByNomeContaining(String nome);


    boolean existsByCpf(String cpf);

}
