package org.wayster.com.emprestimos.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<EmprestimoEntity, Long> {

    List<EmprestimoEntity> findByClienteId(Long clienteId);

    boolean existsByClienteId(Long clienteId);

    List<EmprestimoEntity> findByDataVencimento(LocalDate dataVencimento);

    @Query(value = """
    SELECT 
        MONTH(e.dataEmprestimo) AS mes,
        SUM(e.valorComJuros) AS totalEmprestado,
        SUM(e.valorDevidoApenasMostrar) AS retornoEsperado,
        COUNT(DISTINCT e.cliente.id) AS clientes,
        AVG(e.taxaJuros) AS mediaJuros
    FROM EmprestimoEntity e
    GROUP BY MONTH(e.dataEmprestimo)
    ORDER BY MONTH(e.dataEmprestimo)
""")
    List<Object[]> buscarResumoMensal();
}
