import React, { useState } from 'react';
import axios from 'axios';

function CadastrarEmprestimo() {
    const [clienteId, setClienteId] = useState('');
    const [valorEmprestimo, setValorEmprestimo] = useState('');
    const [taxaJuros, setTaxaJuros] = useState('');

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            const emprestimoDto = {
                clienteId: Number(clienteId),
                valorEmprestimo: Number(valorEmprestimo),
                taxaJuros: Number(taxaJuros),
            };

            const response = await axios.post('http://localhost:8080/emprestimos', emprestimoDto);
            alert('Empréstimo cadastrado com sucesso!');
            console.log('Retorno do backend:', response.data);
        } catch (error) {
            alert('Erro ao cadastrar empréstimo.');
            console.error(error);
        }
    };

    return (
        <div style={{ maxWidth: '400px', margin: '0 auto' }}>
            <h2>Cadastrar Empréstimo</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Cliente ID:</label>
                    <input
                        type="number"
                        value={clienteId}
                        onChange={(e) => setClienteId(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Valor Empréstimo:</label>
                    <input
                        type="number"
                        value={valorEmprestimo}
                        onChange={(e) => setValorEmprestimo(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Taxa de Juros (%):</label>
                    <input
                        type="number"
                        value={taxaJuros}
                        onChange={(e) => setTaxaJuros(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Cadastrar</button>
            </form>
        </div>
    );
}

export default CadastrarEmprestimo;