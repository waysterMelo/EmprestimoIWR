package org.wayster.com.emprestimos.EmprestimoUtils;

import java.util.Optional;

public class EmprestimoUtils {


    /**
     * Calcula o valor final de um empréstimo com juros.
     *
     * @param valorPrincipal Valor inicial do empréstimo.
     * @param taxaJuros Taxa de juros em formato decimal (ex.: 0.15 para 15%).
     * @return Valor total com juros.
     * @throws IllegalArgumentException Se os parâmetros forem inválidos.
     */
    public static Double calcularValorComJuros(Double valorPrincipal, Double taxaJuros){
        return Optional.of(valorPrincipal)
                .filter(valor -> valor > 0) // Verifica se o valor do empréstimo é válido (positivo)
                .flatMap(valor -> Optional.of(taxaJuros)
                        .filter(taxa -> taxa >= 0 && taxa <= 1) // Verifica se a taxa de juros é válida (entre 0 e 100%)
                        .map(taxa -> valor + (valor * taxa))) // Aplica o cálculo
                .orElseThrow(() -> new IllegalArgumentException("Valor do empréstimo ou taxa de juros inválidos."));
    }
}
