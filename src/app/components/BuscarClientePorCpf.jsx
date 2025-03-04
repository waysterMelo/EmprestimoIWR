import React, { useState } from 'react';
import axios from 'axios';

function BuscarClientePorCpf() {
    const [cpf, setCpf] = useState('');
    const [cliente, setCliente] = useState(null);
    const [emprestimos, setEmprestimos] = useState([]);

    const handleBuscar = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/clientes/cpf/${cpf}`);
            setCliente(response.data);
            setEmprestimos(response.data.emprestimos || []);
        } catch (error) {
            alert('Cliente não encontrado ou erro ao buscar.');
            console.error(error);
        }
    };

    return (
        <div style={{ maxWidth: '600px', margin: '0 auto' }}>
            <h2>Buscar Cliente por CPF</h2>
            <div>
                <input
                    type="text"
                    placeholder="Digite o CPF"
                    value={cpf}
                    onChange={(e) => setCpf(e.target.value)}
                />
                <button onClick={handleBuscar}>Buscar</button>
            </div>

            {cliente && (
                <div style={{ marginTop: '20px' }}>
                    <h3>Dados do Cliente</h3>
                    <p><b>Nome:</b> {cliente.nome}</p>
                    <p><b>CPF:</b> {cliente.cpf}</p>

                    <h4>Empréstimos:</h4>
                    <ul>
                        {emprestimos.map((emp) => (
                            <li key={emp.id}>
                                Empréstimo #{emp.id} - Valor: {emp.valorEmprestimo} - Status: {emp.statusPagamento}
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
}

export default BuscarClientePorCpf;