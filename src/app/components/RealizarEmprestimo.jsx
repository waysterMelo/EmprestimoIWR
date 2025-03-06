import React, { useState, useEffect } from "react";
import { DollarSign, ArrowLeft, Search, Save } from "lucide-react";
import { useNavigate } from "react-router-dom";
import EmprestimoServices from "../services/EmprestimoServices";
import ClientesServices from "../services/ClientesServices";
import "../css/CadastrarClientes.css";

const RealizarEmprestimo = () => {
    const navigate = useNavigate();
    const emprestimoService = new EmprestimoServices();
    const clientesService = new ClientesServices();

    const [buscaNome, setBuscaNome] = useState("");
    const [buscaCpf, setBuscaCpf] = useState("");
    const [cliente, setCliente] = useState(null);
    const [valor, setValor] = useState("");
    const [juros, setJuros] = useState("");
    const [valorFinal, setValorFinal] = useState(0);

    const buscarClientePorNome = async () => {
        try {
            const resultado = await clientesService.buscarClientePorNome(buscaNome);
            setCliente(resultado.data);
        } catch (error) {
            alert("Cliente não encontrado pelo nome.");
            setCliente(null);
        }
    };

    const buscarClientePorCpf = async () => {
        try {
            const resultado = await clientesService.buscarClientePorCpf(buscaCpf);
            setCliente(resultado.data);
        } catch (error) {
            alert("Cliente não encontrado pelo CPF.");
            setCliente(null);
        }
    };

    const calcularValorFinal = () => {
        const valorNum = parseFloat(valor);
        const jurosNum = parseFloat(juros);
        if (!isNaN(valorNum) && !isNaN(jurosNum)) {
            setValorFinal(valorNum + (valorNum * jurosNum) / 100);
        } else {
            setValorFinal(0);
        }
    };

    useEffect(() => {
        calcularValorFinal();
    }, [valor, juros]);

    const handleSalvar = async () => {
        if (!cliente || valorFinal <= 0) {
            alert("Complete corretamente todas as informações antes de salvar.");
            return;
        }

        try {
            await emprestimoService.realizarEmprestimo({
                clienteId: cliente.id,
                valorInicial: valor,
                juros,
                valorFinal,
            });
            alert("Empréstimo realizado com sucesso!");
            setCliente(null);
            setBuscaNome("");
            setBuscaCpf("");
            setValor("");
            setJuros("");
            setValorFinal(0);
        } catch (error) {
            alert("Erro ao realizar empréstimo: " + error.message);
        }
    };

    return (
        <div className="container-fluid py-5 background">
            <div className="row justify-content-center">
                <div className="col-md-8 col-lg-12">
                    <div className="card shadow-lg border-0 rounded-lg position-relative">
                        <button
                            onClick={() => navigate('/')}
                            className="btn btn-dark position-absolute top-0 start-0 m-3 d-flex align-items-center"
                            style={{ zIndex: 10 }}
                        >
                            <ArrowLeft size={20} className="me-2 text-info" />
                            Tela Inicial
                        </button>

                        <div className="bg-gradient-primary text-white text-center py-3">
                            <DollarSign size={48} className="mb-2" />
                            <h2 className="display-6 mb-0 text-dark">Realizar Empréstimo</h2>
                        </div>

                        <div className="card-body p-4">
                            <div className="row mb-3">
                                <div className="col-md-6">
                                    <div className="input-group">
                                        <input
                                            type="text"
                                            className="form-control"
                                            placeholder="Buscar Cliente por Nome"
                                            value={buscaNome}
                                            onChange={(e) => setBuscaNome(e.target.value)}
                                        />
                                        <button className="btn btn-primary" onClick={buscarClientePorNome}>
                                            <Search size={20} />
                                        </button>
                                    </div>
                                </div>
                                <div className="col-md-6">
                                    <div className="input-group">
                                        <input
                                            type="text"
                                            className="form-control"
                                            placeholder="Buscar por CPF"
                                            value={buscaCpf}
                                            onChange={(e) => setBuscaCpf(e.target.value)}
                                        />
                                        <button className="btn btn-secondary" onClick={buscarClientePorCpf}>
                                            <Search size={20} />
                                        </button>
                                    </div>
                                </div>
                            </div>

                            {cliente && (
                                <div className="alert alert-info mb-3">
                                    <div className="row">
                                        <div className="col-md-3"><strong>ID:</strong> {cliente.id}</div>
                                        <div className="col-md-3"><strong>Nome:</strong> {cliente.nome}</div>
                                        <div className="col-md-3"><strong>CPF:</strong> {cliente.cpf}</div>
                                        <div className="col-md-3"><strong>Telefone:</strong> {cliente.telefone}</div>
                                    </div>
                                </div>
                            )}

                            <input
                                type="number"
                                className="form-control mb-2"
                                placeholder="Valor a emprestar"
                                value={valor}
                                onChange={(e) => setValor(e.target.value)}
                            />

                            <input
                                type="number"
                                className="form-control mb-2"
                                placeholder="Juros (%)"
                                value={juros}
                                onChange={(e) => setJuros(e.target.value)}
                            />

                            <div className="alert alert-success text-center mb-3">
                                Valor Total com Juros: <strong>R$ {valorFinal.toFixed(2)}</strong>
                            </div>

                            <button className="btn btn-success w-100" onClick={handleSalvar}>
                                <Save size={20} className="me-2" /> Salvar Empréstimo
                            </button>
                        </div>

                        <div className="card-footer text-center bg-light py-3">
                            <small className="text-muted">
                                Os dados do empréstimo estão sujeitos às políticas internas da empresa.
                            </small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default RealizarEmprestimo;