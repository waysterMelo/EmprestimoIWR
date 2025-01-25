package org.wayster.com.emprestimos.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wayster.com.emprestimos.Dto.EmprestimoDto;
import org.wayster.com.emprestimos.EmprestimoUtils.EmprestimoUtils;
import org.wayster.com.emprestimos.Entity.ClientesEntity;
import org.wayster.com.emprestimos.Entity.EmprestimoEntity;
import org.wayster.com.emprestimos.Enums.StatusPagamento;
import org.wayster.com.emprestimos.Mapper.MapperEmprestimo;
import org.wayster.com.emprestimos.Repository.ClientesRepository;
import org.wayster.com.emprestimos.Repository.EmprestimoRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final ClientesRepository clientesRepository;
    private final MapperEmprestimo emprestimosMapper;
    private final ClientesServices clientesServices;

    /**
     * Cadastra um novo empréstimo.
     *
     * @param emprestimoDto Dados do empréstimo a ser cadastrado.
     * @return O DTO do empréstimo cadastrado.
     */
        public Optional<EmprestimoDto> cadastrarEmprestimo(EmprestimoDto emprestimoDto){
            // Verifica se o cliente existe no banco
            return clientesRepository.findById(emprestimoDto.getClienteId())
                    .map(cliente -> {
                        // Calcula o valor com juros
                        double valorComJuros = EmprestimoUtils.calcularValorComJuros(emprestimoDto.getValorEmprestimo(), emprestimoDto.getTaxaJuros());
                        emprestimoDto.setValorComJuros(valorComJuros);

                        // Define a data do empréstimo como a data atual, caso não esteja preenchida
                        emprestimoDto.setDataEmprestimo(Optional.ofNullable(emprestimoDto.getDataEmprestimo()).orElse(LocalDate.now()));

                        // Define a data de vencimento para 30 dias após a data do empréstimo, caso não esteja preenchida
                        emprestimoDto.setDataVencimento(Optional.ofNullable(emprestimoDto.getDataVencimento())
                                .orElse(emprestimoDto.getDataEmprestimo().plusDays(30)));

                        // Define o status do pagamento como PENDENTE por padrão
                        emprestimoDto.setStatusPagamento(Optional.ofNullable(emprestimoDto.getStatusPagamento()).orElse(StatusPagamento.PENDENTE));

                        // Mapeia o DTO para a entidade do empréstimo
                        EmprestimoEntity emprestimoEntity = emprestimosMapper.toEntity(emprestimoDto, cliente);

                        // Salva o empréstimo no banco de dados
                        EmprestimoEntity emprestimoSalvo = emprestimoRepository.save(emprestimoEntity);

                        // Retorna o empréstimo cadastrado como DTO
                        return emprestimosMapper.toDto(emprestimoSalvo);
                    });
        }
}
