import React, { useState } from 'react';
import axios from 'axios';

function PagarParcialmente() {
    const [emprestimoId, setEmprestimoId] = useState('');
    const [valorPago, setValorPago] = useState('');

    const handlePagamentoParcial = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.put(
                `http://localhost:8080/emprestimos/${emprestimoId}/pagar-parcial`,
                null, // ou {} se preferir
                {
                    params: {
                        valorPago: Number(valorPago),
                    },
                }
            );
            alert('Pagamento parcial realizado com sucesso!');
            console.log(response.data);
        } catch (error) {
            alert('Erro ao efetuar pagamento parcial.');
            console.error(error);
        }
    };

    return (
        <div style={{ maxWidth: '400px', margin: '0 auto' }}>
            <h2>Pagamento Parcial</h2>
            <form onSubmit={handlePagamentoParcial}>
                <div>
                    <label>ID do Empréstimo:</label>
                    <input
                        type="number"
                        value={emprestimoId}
                        onChange={(e) => setEmprestimoId(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Valor Pago:</label>
                    <input
                        type="number"
                        value={valorPago}
                        onChange={(e) => setValorPago(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Pagar Parcialmente</button>
            </form>
        </div>
    );
}

export default PagarParcialmente;