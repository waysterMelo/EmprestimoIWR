package org.wayster.com.emprestimos.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.wayster.com.emprestimos.Dto.ResumoFinanceiroMensalDTO;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;
import org.wayster.com.emprestimos.Enums.StatusPagamento;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<EmprestimoEntity, Long> {

    List<EmprestimoEntity> findByClienteId(Long clienteId);

    boolean existsByClienteId(Long clienteId);


    List<EmprestimoEntity> findByDataVencimentoAndStatusPagamento(LocalDate dataVencimento, StatusPagamento statusPagamento);

    @Query(value = """
        SELECT
            MONTH(e.dataEmprestimo) AS mes,
           
            SUM(CASE WHEN e.statusPagamento IN ('PENDENTE', 'ATRASADO') THEN e.valorEmprestimo ELSE 0 END) AS totalEmprestado,
            SUM(CASE WHEN e.statusPagamento IN ('PENDENTE', 'ATRASADO') THEN e.valorDevidoApenasMostrar ELSE 0 END) AS retornoEsperado,
            COUNT(DISTINCT e.cliente.id) AS clientes,
            AVG(e.taxaJuros) AS mediaJuros
        FROM EmprestimoEntity e
        GROUP BY MONTH(e.dataEmprestimo)
        ORDER BY MONTH(e.dataEmprestimo)
    """)
    List<Object[]> buscarResumoMensal();

    @Query("""
        SELECT
            MONTH(e.dataEmprestimo) AS mes,
            COALESCE(SUM(e.valorEmprestimo), 0.0) AS totalEmprestado,
            COALESCE(SUM(e.valorComJuros), 0.0) AS retornoEsperado,
            COALESCE(SUM(e.valorComJuros) - SUM(e.valorEmprestimo), 0.0) AS lucro
        FROM EmprestimoEntity e
        GROUP BY MONTH(e.dataEmprestimo)
        ORDER BY mes
    """)
    List<Object[]> buscarResumoFinanceiroMensal();


    List<EmprestimoEntity> findByDataVencimento(LocalDate dataVencimento);
}
