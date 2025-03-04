import React, { useState } from 'react';
import axios from 'axios';

function RealizarBaixaPagamento() {
    const [emprestimoId, setEmprestimoId] = useState('');

    const handleBaixa = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.put(
                `http://localhost:8080/emprestimos/${emprestimoId}/baixa-pagamento`
            );
            alert('Pagamento integral realizado (Baixa) com sucesso!');
            console.log(response.data);
        } catch (error) {
            alert('Erro ao efetuar baixa de pagamento.');
            console.error(error);
        }
    };

    return (
        <div style={{ maxWidth: '400px', margin: '0 auto' }}>
            <h2>Baixa de Pagamento</h2>
            <form onSubmit={handleBaixa}>
                <div>
                    <label>ID do Empréstimo:</label>
                    <input
                        type="number"
                        value={emprestimoId}
                        onChange={(e) => setEmprestimoId(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Baixar Pagamento</button>
            </form>
        </div>
    );
}

export default RealizarBaixaPagamento;