import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import ConsultarClienteService from "../services/ConsultarClienteService";
import "../css/ModalPagamento.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
    faSearch,
    faUser,
    faIdCard,
    faExclamationTriangle
} from "@fortawesome/free-solid-svg-icons";
import "../css/ConsultarCliente.css";

const ConsultarCliente = () => {
    const [cpf, setCpf] = useState("");
    const [cliente, setCliente] = useState(null);
    const [emprestimos, setEmprestimos] = useState([]);
    const [erro, setErro] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();
    const [foto, setFoto] = useState(null);
    const [emprestimoSelecionado, setEmprestimoSelecionado] = useState(null);
    const [valorParcial, setValorParcial] = useState("");
    const [erroPagamento, setErroPagamento] = useState("");
    const [isProcessing, setIsProcessing] = useState(false);

    const abrirModalEmprestimo = (emprestimo) => {
        setEmprestimoSelecionado(emprestimo);
        setValorParcial(""); // Limpa o campo de pagamento parcial
        setErroPagamento(""); // Limpa erros anteriores
    };

    const formatCpf = (value) => {
        const numerosApenas = value.replace(/\D/g, "");
        if (numerosApenas.length <= 11) {
            return numerosApenas
                .replace(/(\d{3})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
        }
        return numerosApenas.slice(0, 11)
            .replace(/(\d{3})(\d)/, "$1.$2")
            .replace(/(\d{3})(\d)/, "$1.$2")
            .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
    };

    const handleChangeCpf = (e) => {
        const formattedCpf = formatCpf(e.target.value);
        setCpf(formattedCpf);
    };

    const handleBuscarCliente = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setErro("");

        try {
            const cpfNumerico = cpf.replace(/\D/g, "");
            if (cpfNumerico.length !== 11) {
                setErro("Por favor, digite um CPF válido com 11 dígitos.");
                setIsLoading(false);
                return;
            }

            const response = await ConsultarClienteService.buscarClienteComEmprestimosPorCpf(cpfNumerico);
            if (response.data) {
                setCliente(response.data.cliente);
                setEmprestimos(response.data.emprestimos);


                const fotoResponse = await ConsultarClienteService.buscarFotoClinte(cpfNumerico);
                const imgUrl = URL.createObjectURL(fotoResponse.data);
                setFoto(imgUrl);

            }

        } catch (error) {
            console.error(error);
            setCliente(null);
            setEmprestimos([]);
            setFoto(null)
            setErro("Cliente não encontrado ou houve um erro na busca.");
        } finally {
            setIsLoading(false);
        }
    };

    const handleAtualizarCliente = () => {
        if (cliente && cliente.id) {
            navigate(`/atualizar-cliente/${cliente.id}`);
        } else {
            alert("Cliente não encontrado para atualizar.");
        }
    };

    const formatarData = (dataString) => {
        const data = new Date(dataString);
        return data.toLocaleDateString('pt-BR');
    };

    const formatarValor = (valor) => {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(valor);
    };

    const handleQuitarEmprestimo = async () => {
        if (!emprestimoSelecionado) return;
        setIsProcessing(true);

        try {
            await ConsultarClienteService.quitarEmprestimo(emprestimoSelecionado.id);
            alert("Empréstimo quitado com sucesso!");
            setEmprestimoSelecionado(null); // Fecha o modal
            window.location.reload(); // Recarrega os dados da página
        } catch (error) {
            setErroPagamento("Erro ao quitar o empréstimo.");
        } finally {
            setIsProcessing(false);
        }
    };

    const handlePagamentoParcial = async () => {
        if (!emprestimoSelecionado || valorParcial <= 0) {
            setErroPagamento("Digite um valor válido.");
            return;
        }

        setIsProcessing(true);

        try {
            await ConsultarClienteService.pagarParcialmente(emprestimoSelecionado.id, parseFloat(valorParcial));
            alert(`Pagamento parcial de R$${valorParcial} realizado com sucesso!`);
            setEmprestimoSelecionado(null); // Fecha o modal
            window.location.reload(); // Recarrega os dados da página
        } catch (error) {
            setErroPagamento("Erro ao processar pagamento parcial.");
        } finally {
            setIsProcessing(false);
        }
    };

    return (
        <div className="background-consultar">
            <div className="container consultar-container">
                <div className="card-consultar">
                    <div className="header-consultar">
                        <h1>
                            <FontAwesomeIcon icon={faUser} className="me-2" />
                            Consultar Cliente
                        </h1>
                    </div>

                    <div className="container p-4">
                        {/* Formulário de busca */}
                        <form onSubmit={handleBuscarCliente} className="search-form">
                            <div className="mb-3">
                                <label htmlFor="cpf" className="form-label fw-bold">
                                    <FontAwesomeIcon icon={faIdCard} className="me-2" />
                                    CPF do Cliente
                                </label>
                                <div className="input-group">
                                    <input
                                        type="text"
                                        id="cpf"
                                        className="form-control"
                                        placeholder="000.000.000-00"
                                        value={cpf}
                                        onChange={handleChangeCpf}
                                        required
                                    />
                                    <button type="submit" className="btn btn-primary">
                                        <FontAwesomeIcon icon={faSearch} className="me-2" />
                                        Buscar
                                    </button>
                                </div>
                            </div>
                        </form>

                        {/* Exibindo mensagem de erro */}
                        {erro && (
                            <div className="alert alert-danger mt-3">
                                <FontAwesomeIcon icon={faExclamationTriangle} className="me-2" />
                                {erro}
                            </div>
                        )}

                        {/* Indicador de carregamento */}
                        {isLoading && (
                            <div className="text-center mt-4">
                                <div className="spinner-border text-primary" role="status">
                                    <span className="visually-hidden">Carregando...</span>
                                </div>
                                <p className="mt-2">Buscando informações do cliente...</p>
                            </div>
                        )}

                        {/* Se encontrou o cliente e não está carregando, exibe tudo junto */}
                        {cliente && !isLoading && (
                            <>

                                {/* Exibindo dados do cliente */}
                                {cliente && !isLoading && (
                                    <div className="cliente-info mt-4">
                                        <div className="card shadow-sm border-0 rounded-lg">
                                            <div className="card-header bg-primary text-white py-3">
                                                <h2 className="mb-0 fs-4">Informações do Cliente</h2>
                                            </div>
                                            <div className="card-body p-4">
                                                <div className="row">
                                                    {/* FOTO DO CLIENTE */}
                                                    <div className="col-12 text-center mb-4">
                                                        {foto ? (
                                                            <img
                                                                src={foto}
                                                                alt="Foto do Cliente"
                                                                className="img-fluid rounded-circle"
                                                                style={{ width: "150px", height: "150px", objectFit: "cover" }}
                                                            />
                                                        ) : (
                                                            <div className="placeholder-foto">
                                                                <span className="text-muted">Sem Foto</span>
                                                            </div>
                                                        )}
                                                    </div>

                                                    {/* PRIMEIRA COLUNA */}
                                                    <div className="col-md-6">
                                                        <div className="info-group p-3 bg-light rounded mb-3">
                                                            <div className="d-flex align-items-center mb-2">
                                                                <i className="bi bi-person-fill fs-4 me-3 text-primary"></i>
                                                                <div>
                                                                    <small className="text-muted d-block">Nome</small>
                                                                    <span className="fw-bold">{cliente.nome}</span>
                                                                </div>
                                                            </div>

                                                            <div className="d-flex align-items-center mb-2">
                                                                <i className="bi bi-credit-card-2-front fs-4 me-3 text-primary"></i>
                                                                <div>
                                                                    <small className="text-muted d-block">CPF</small>
                                                                    <span className="fw-bold">{formatCpf(cliente.cpf)}</span>
                                                                </div>
                                                            </div>

                                                            <div className="d-flex align-items-center mb-2">
                                                                <i className="bi bi-envelope-fill fs-4 me-3 text-primary"></i>
                                                                <div>
                                                                    <small className="text-muted d-block">E-mail</small>
                                                                    <span className="fw-bold">{cliente.email}</span>
                                                                </div>
                                                            </div>

                                                            <div className="d-flex align-items-center">
                                                                <i className="bi bi-telephone-fill fs-4 me-3 text-primary"></i>
                                                                <div>
                                                                    <small className="text-muted d-block">Telefone</small>
                                                                    <span className="fw-bold">{cliente.telefone}</span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    {/* SEGUNDA COLUNA */}
                                                    <div className="col-md-6">
                                                        <div className="info-group p-3 bg-light rounded">
                                                            <div className="d-flex align-items-center mb-2">
                                                                <i className="bi bi-geo-alt-fill fs-4 me-3 text-primary"></i>
                                                                <div>
                                                                    <small className="text-muted d-block">Endereço</small>
                                                                    <span className="fw-bold">{cliente.endereco}, {cliente.numero}</span>
                                                                </div>
                                                            </div>

                                                            <div className="d-flex align-items-center mb-2">
                                                                <i className="bi bi-building fs-4 me-3 text-primary"></i>
                                                                <div>
                                                                    <small className="text-muted d-block">Bairro</small>
                                                                    <span className="fw-bold">{cliente.bairro}</span>
                                                                </div>
                                                            </div>

                                                            <div className="d-flex align-items-center mb-2">
                                                                <i className="bi bi-flag fs-4 me-3 text-primary"></i>
                                                                <div>
                                                                    <small className="text-muted d-block">Cidade</small>
                                                                    <span className="fw-bold">{cliente.cidade}</span>
                                                                </div>
                                                            </div>

                                                            <div className="d-flex align-items-center">
                                                                <i className="bi bi-globe fs-4 me-3 text-primary"></i>
                                                                <div>
                                                                    <small className="text-muted d-block">Estado</small>
                                                                    <span className="fw-bold">{cliente.estado}</span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                {/* BOTÃO ATUALIZAR */}
                                                <div className="d-flex justify-content-end mt-4">
                                                    <button className="btn btn-primary" onClick={handleAtualizarCliente}>
                                                        <i className="bi bi-pencil-square me-2"></i>
                                                        Atualizar Dados
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                )}

                                {/* LISTA DE EMPRÉSTIMOS */}
                                <h2 className="mb-4 mt-5">Empréstimos do Cliente</h2>
                                {emprestimos.length > 0 ? (
                                    <div className="table-responsive">
                                        <table className="table table-striped table-hover">
                                            <thead className="table-dark">
                                            <tr>
                                                <th>ID</th>
                                                <th>Valor</th>
                                                <th>Taxa de Juros</th>
                                                <th>Data de Contratação</th>
                                                <th>Data de Vencimento</th>
                                                <th>Status</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            {emprestimos.map((emprestimo) => (
                                                <tr key={emprestimo.id} onClick={() => abrirModalEmprestimo(emprestimo)} style={{ cursor: "pointer" }}>
                                                    <td>{emprestimo.id}</td>
                                                    <td>{formatarValor(emprestimo.valorEmprestimo)}</td>
                                                    <td>{emprestimo.taxaJuros * 100}%</td>
                                                    <td>{formatarData(emprestimo.dataEmprestimo)}</td>
                                                    <td>{formatarData(emprestimo.dataVencimento)}</td>
                                                    <td>
                                                      <span className={`badge ${emprestimo.status === 'ATIVO' ? 'bg-success' :
                                                          emprestimo.status === 'ATRASADO' ? 'bg-danger' :
                                                              emprestimo.status === 'QUITADO' ? 'bg-info' : 'bg-warning'}`}>
                                                        {emprestimo.status}
                                                      </span>
                                                    </td>
                                                </tr>
                                            ))}
                                            </tbody>
                                        </table>
                                    </div>
                                ) : (
                                    <div className="alert alert-info">
                                        Este cliente não possui empréstimos registrados.
                                    </div>
                                )}
                            </>
                        )}
                        {/* MODAL PARA PAGAMENTO */}
                        {emprestimoSelecionado && (
                            <div className="modal fade show d-block modal-pagamento-overlay">
                                <div className="modal-dialog modal-pagamento-dialog">
                                    <div className="modal-content modal-pagamento-content">

                                        {/* Cabeçalho do Modal */}
                                        <div className="modal-header modal-pagamento-header">
                                            <h5 className="modal-title modal-pagamento-title">Gerenciar Pagamento</h5>
                                            <button
                                                className="btn-close modal-pagamento-close"
                                                onClick={() => setEmprestimoSelecionado(null)}
                                            ></button>
                                        </div>

                                        {/* Corpo do Modal */}
                                        <div className="modal-body modal-pagamento-body">
                                            {/* Informações do Empréstimo */}
                                            <div className="modal-pagamento-info-group">
                                                <div className="modal-pagamento-info-item">
                                                    <strong className="modal-pagamento-info-label">ID:</strong>
                                                    <span className="modal-pagamento-info-value">{emprestimoSelecionado.id}</span>
                                                </div>
                                                <div className="modal-pagamento-info-item">
                                                    <strong className="modal-pagamento-info-label">Valor Total:</strong>
                                                    <span className="modal-pagamento-valor">
                R$ {formatarValor(emprestimoSelecionado.valorEmprestimo)}
              </span>
                                                </div>
                                                <div className="modal-pagamento-info-item">
                                                    <strong className="modal-pagamento-info-label">Status:</strong>
                                                    <span
                                                        className={`modal-pagamento-badge ${
                                                            emprestimoSelecionado.statusPagamento === "Ativo"
                                                                ? "modal-pagamento-badge-ativo"
                                                                : "modal-pagamento-badge-quitado"
                                                        }`}
                                                    >
                {emprestimoSelecionado.statusPagamento}
              </span>
                                                </div>
                                                <div className="modal-pagamento-info-item">
                                                    <strong className="modal-pagamento-info-label">Data de Vencimento:</strong>
                                                    <span className="modal-pagamento-info-value">{formatarData(emprestimoSelecionado.dataVencimento)}</span>
                                                </div>
                                            </div>

                                            {/* Pagamento Parcial */}
                                            <div className="mb-3">
                                                <label className="form-label modal-pagamento-form-label">Pagamento Parcial (R$)</label>
                                                <div className="input-group">
                                                    <span className="input-group-text modal-pagamento-input-prefix">R$</span>
                                                    <input
                                                        type="number"
                                                        className="form-control modal-pagamento-input"
                                                        value={valorParcial}
                                                        onChange={(e) => setValorParcial(e.target.value)}
                                                        disabled={isProcessing}
                                                        placeholder="Insira o valor do pagamento"
                                                    />
                                                </div>
                                            </div>

                                            {/* Exibir erro se houver */}
                                            {erroPagamento && (
                                                <div className="modal-pagamento-erro">
                                                    <i className="bi bi-exclamation-circle me-2"></i>
                                                    {erroPagamento}
                                                </div>
                                            )}
                                        </div>

                                        {/* Footer com Botões */}
                                        <div className="modal-footer modal-pagamento-footer">
                                            <div className="modal-pagamento-botoes">
                                                <button
                                                    className="modal-pagamento-btn-parcial"
                                                    onClick={handlePagamentoParcial}
                                                    disabled={isProcessing}
                                                >
                                                    {isProcessing ? (
                                                        <>
                                                            <span className="spinner-border spinner-border-sm modal-pagamento-spinner" role="status" aria-hidden="true"></span>
                                                            Processando...
                                                        </>
                                                    ) : (
                                                        "Quitar Parcialmente"
                                                    )}
                                                </button>

                                                <button
                                                    className="modal-pagamento-btn-total"
                                                    onClick={handleQuitarEmprestimo}
                                                    disabled={isProcessing}
                                                >
                                                    {isProcessing ? (
                                                        <>
                                                            <span className="spinner-border spinner-border-sm modal-pagamento-spinner" role="status" aria-hidden="true"></span>
                                                            Processando...
                                                        </>
                                                    ) : (
                                                        "Quitar Totalmente"
                                                    )}
                                                </button>
                                            </div>

                                            <button
                                                className="modal-pagamento-btn-fechar"
                                                onClick={() => setEmprestimoSelecionado(null)}
                                            >
                                                Fechar
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>

    );
};

export default ConsultarCliente;
