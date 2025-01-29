package org.wayster.com.emprestimos.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wayster.com.emprestimos.Dto.EmprestimoDto;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;
import org.wayster.com.emprestimos.Enums.StatusPagamento;

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

    /**
     * Busca todos os empréstimos vencidos em uma data específica.
     *
     * @param dataVencimento A data de vencimento dos empréstimos.
     * @return Lista de empréstimos vencidos.
     */
    List<EmprestimoEntity> findByDataVencimento(LocalDate dataVencimento);


}
