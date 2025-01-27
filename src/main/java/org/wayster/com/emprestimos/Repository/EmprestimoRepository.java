package org.wayster.com.emprestimos.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wayster.com.emprestimos.Dto.EmprestimoDto;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<EmprestimoEntity, Long> {

    /**
     * Busca todos os empréstimos de um cliente pelo ID do cliente.
     *
     * @param clienteId ID do cliente.
     * @return Lista de empréstimos associados.
     */
    List<EmprestimoEntity> findByClienteId(Long clienteId);

    /**
     * Verifica se existe pelo menos um empréstimo associado a um cliente.
     *
     * @param clienteId ID do cliente.
     * @return true se existir pelo menos um empréstimo.
     */
    boolean existsByClienteId(Long clienteId);

}
