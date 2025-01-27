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

    /**
     * Busca um cliente pelo CPF.
     *
     * @param cpf CPF do cliente.
     * @return Optional contendo a entidade do cliente, se encontrada.
     */
    Optional<ClientesEntity> findByCpf(Long cpf);


    /**
     * Retorna todos os clientes cadastrados.
     *
     * @return Lista de todos os clientes.
     */
    List<ClientesEntity> findAll();


    /**
     * Busca clientes pelo nome com base em palavras-chave.
     *
     * @param nome Palavra-chave parcial ou completa para buscar clientes.
     * @return Lista de clientes correspondentes.
     */
    @Query("select c from ClientesEntity c where LOWER(c.nome) LIKE  lower(concat('%', :nome, '%') ) ")
    List<ClientesEntity> findByNomeContaining(String nome);

    /**
     * Verifica se um cliente com o CPF informado já existe.
     *
     * @param cpf CPF do cliente.
     * @return True se existir, False caso contrário.
     */
    boolean existsByCpf(Long cpf);
}
