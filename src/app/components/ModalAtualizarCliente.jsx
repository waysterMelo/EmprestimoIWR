import React, { useState, useEffect } from "react";
import "../css/ModalAtualizarCliente.css";
import ClientesServices from "../services/ClientesServices";

const ModalAtualizarCliente = ({ cliente, onClose, onClienteAtualizado }) => {
    const [formData, setFormData] = useState({
        nome: "",
        cpf: "",
        email: "",
        telefone: "",
        endereco: "",
        numero: "",
        bairro: "",
        cidade: "",
        estado: "",
    });
    const [foto, setFoto] = useState(null);
    const [fotoPreview, setFotoPreview] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [erro, setErro] = useState("");
    const [sucesso, setSucesso] = useState("");
    const [formStep, setFormStep] = useState(1);
    const totalSteps = 2;

    const clientesService = new ClientesServices();

    useEffect(() => {
        if (cliente) {
            setFormData({
                nome: cliente.nome || "",
                cpf: cliente.cpf || "",
                email: cliente.email || "",
                telefone: cliente.telefone || "",
                endereco: cliente.endereco || "",
                numero: cliente.numero || "",
                bairro: cliente.bairro || "",
                cidade: cliente.cidade || "",
                estado: cliente.estado || "",
            });

            // Se já houver uma foto do cliente, exiba-a
            if (cliente.fotoUrl) {
                setFotoPreview(cliente.fotoUrl);
            }
        }
    }, [cliente]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value,
        });
    };

    const handleFotoChange = (e) => {
        if (e.target.files && e.target.files[0]) {
            const selectedFoto = e.target.files[0];
            setFoto(selectedFoto);

            // Criar preview da imagem
            const reader = new FileReader();
            reader.onloadend = () => {
                setFotoPreview(reader.result);
            };
            reader.readAsDataURL(selectedFoto);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setErro("");
        setSucesso("");

        try {
            // Atualizar cliente usando o serviço
            await clientesService.atualizarCliente(cliente.id, formData, foto);
            setSucesso("Cliente atualizado com sucesso!");

            // Notificar componente pai sobre a atualização
            if (onClienteAtualizado) {
                onClienteAtualizado();
            }

            // Fechar o modal após um breve atraso
            setTimeout(() => {
                onClose();
            }, 2000);
        } catch (error) {
            setErro(`Erro ao atualizar cliente: ${error.message}`);
        } finally {
            setIsLoading(false);
        }
    };

    const nextStep = () => {
        setFormStep(current => Math.min(current + 1, totalSteps));
    };

    const prevStep = () => {
        setFormStep(current => Math.max(current - 1, 1));
    };

    // Função para detectar clique fora do modal para fechar
    const handleClickOutside = (e) => {
        if (e.target.className === 'modal-overlay') {
            onClose();
        }
    };

    return (
        <div className="modal-overlay" onClick={handleClickOutside}>
            <div className="modal-atualizar">
                <div className="modal-header">
                    <h2>Atualizar Dados do Cliente</h2>
                    <button className="btn-close" onClick={onClose}></button>
                </div>
                <div className="modal-body">
                    <form onSubmit={handleSubmit}>
                        <div className="progress-container" style={{ marginBottom: '2rem' }}>
                            <div className="progress" style={{ height: '8px', borderRadius: '4px', backgroundColor: '#e2e8f0' }}>
                                <div
                                    className="progress-bar"
                                    style={{
                                        width: `${(formStep / totalSteps) * 100}%`,
                                        backgroundColor: '#4361ee',
                                        height: '100%',
                                        borderRadius: '4px',
                                        transition: 'width 0.3s ease'
                                    }}
                                ></div>
                            </div>
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '8px' }}>
                                <span style={{ fontSize: '0.85rem', color: formStep >= 1 ? '#4361ee' : '#94a3b8', fontWeight: formStep === 1 ? '600' : '400' }}>
                                    Informações Pessoais
                                </span>
                                <span style={{ fontSize: '0.85rem', color: formStep >= 2 ? '#4361ee' : '#94a3b8', fontWeight: formStep === 2 ? '600' : '400' }}>
                                    Endereço
                                </span>
                            </div>
                        </div>

                        {formStep === 1 && (
                            <>
                                <div className="foto-container">
                                    <div className="foto-preview">
                                        {fotoPreview ? (
                                            <img src={fotoPreview} alt="Preview da foto" />
                                        ) : (
                                            <div className="foto-placeholder">
                                                <i className="bi bi-person-circle"></i>
                                            </div>
                                        )}
                                    </div>
                                    <div className="foto-upload">
                                        <label htmlFor="foto">
                                            <i className="bi bi-camera"></i> Alterar Foto
                                        </label>
                                        <input
                                            type="file"
                                            id="foto"
                                            accept="image/*"
                                            onChange={handleFotoChange}
                                            style={{ display: "none" }}
                                        />
                                    </div>
                                </div>

                                <div className="row">
                                    <div className="col-md-6 mb-3">
                                        <label htmlFor="nome" className="form-label">Nome</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="nome"
                                            name="nome"
                                            value={formData.nome}
                                            onChange={handleChange}
                                            required
                                            placeholder="Nome completo"
                                        />
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label htmlFor="cpf" className="form-label">CPF</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="cpf"
                                            name="cpf"
                                            value={formData.cpf}
                                            onChange={handleChange}
                                            disabled
                                            style={{ backgroundColor: '#f1f5f9', cursor: 'not-allowed' }}
                                        />
                                        <small style={{ color: '#64748b', fontSize: '0.8rem', marginTop: '4px', display: 'block' }}>
                                            O CPF não pode ser alterado
                                        </small>
                                    </div>
                                </div>

                                <div className="row">
                                    <div className="col-md-6 mb-3">
                                        <label htmlFor="email" className="form-label">Email</label>
                                        <input
                                            type="email"
                                            className="form-control"
                                            id="email"
                                            name="email"
                                            value={formData.email}
                                            onChange={handleChange}
                                            required
                                            placeholder="exemplo@email.com"
                                        />
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label htmlFor="telefone" className="form-label">Telefone</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="telefone"
                                            name="telefone"
                                            value={formData.telefone}
                                            onChange={handleChange}
                                            required
                                            placeholder="(00) 00000-0000"
                                        />
                                    </div>
                                </div>
                            </>
                        )}

                        {formStep === 2 && (
                            <>
                                <div className="row">
                                    <div className="col-md-8 mb-3">
                                        <label htmlFor="endereco" className="form-label">Endereço</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="endereco"
                                            name="endereco"
                                            value={formData.endereco}
                                            onChange={handleChange}
                                            required
                                            placeholder="Rua, Avenida, etc."
                                        />
                                    </div>
                                    <div className="col-md-4 mb-3">
                                        <label htmlFor="numero" className="form-label">Número</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="numero"
                                            name="numero"
                                            value={formData.numero}
                                            onChange={handleChange}
                                            required
                                            placeholder="123"
                                        />
                                    </div>
                                </div>

                                <div className="row">
                                    <div className="col-md-4 mb-3">
                                        <label htmlFor="bairro" className="form-label">Bairro</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="bairro"
                                            name="bairro"
                                            value={formData.bairro}
                                            onChange={handleChange}
                                            required
                                            placeholder="Seu bairro"
                                        />
                                    </div>
                                    <div className="col-md-4 mb-3">
                                        <label htmlFor="cidade" className="form-label">Cidade</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="cidade"
                                            name="cidade"
                                            value={formData.cidade}
                                            onChange={handleChange}
                                            required
                                            placeholder="Sua cidade"
                                        />
                                    </div>
                                    <div className="col-md-4 mb-3">
                                        <label htmlFor="estado" className="form-label">Estado</label>
                                        <select
                                            className="form-select"
                                            id="estado"
                                            name="estado"
                                            value={formData.estado}
                                            onChange={handleChange}
                                            required
                                        >
                                            <option value="">Selecione...</option>
                                            <option value="AC">Acre</option>
                                            <option value="AL">Alagoas</option>
                                            <option value="AP">Amapá</option>
                                            <option value="AM">Amazonas</option>
                                            <option value="BA">Bahia</option>
                                            <option value="CE">Ceará</option>
                                            <option value="DF">Distrito Federal</option>
                                            <option value="ES">Espírito Santo</option>
                                            <option value="GO">Goiás</option>
                                            <option value="MA">Maranhão</option>
                                            <option value="MT">Mato Grosso</option>
                                            <option value="MS">Mato Grosso do Sul</option>
                                            <option value="MG">Minas Gerais</option>
                                            <option value="PA">Pará</option>
                                            <option value="PB">Paraíba</option>
                                            <option value="PR">Paraná</option>
                                            <option value="PE">Pernambuco</option>
                                            <option value="PI">Piauí</option>
                                            <option value="RJ">Rio de Janeiro</option>
                                            <option value="RN">Rio Grande do Norte</option>
                                            <option value="RS">Rio Grande do Sul</option>
                                            <option value="RO">Rondônia</option>
                                            <option value="RR">Roraima</option>
                                            <option value="SC">Santa Catarina</option>
                                            <option value="SP">São Paulo</option>
                                            <option value="SE">Sergipe</option>
                                            <option value="TO">Tocantins</option>
                                        </select>
                                    </div>
                                </div>
                            </>
                        )}

                        {erro && (
                            <div className="alert alert-danger mt-3">
                                <i className="bi bi-exclamation-triangle"></i>
                                <div>{erro}</div>
                            </div>
                        )}

                        {sucesso && (
                            <div className="alert alert-success mt-3">
                                <i className="bi bi-check-circle"></i>
                                <div>{sucesso}</div>
                            </div>
                        )}

                        <div className="modal-footer">
                            {formStep > 1 ? (
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={prevStep}
                                    style={{ flex: '1' }}
                                >
                                    <i className="bi bi-arrow-left"></i> Voltar
                                </button>
                            ) : (
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={onClose}
                                    style={{ flex: '1' }}
                                >
                                    Cancelar
                                </button>
                            )}

                            {formStep < totalSteps ? (
                                <button
                                    type="button"
                                    className="btn btn-primary"
                                    onClick={nextStep}
                                    style={{ flex: '1' }}
                                >
                                    Próximo <i className="bi bi-arrow-right"></i>
                                </button>
                            ) : (
                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                    disabled={isLoading}
                                    style={{ flex: '1' }}
                                >
                                    {isLoading ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                            Salvando...
                                        </>
                                    ) : (
                                        <>Salvar Alterações <i className="bi bi-check2"></i></>
                                    )}
                                </button>
                            )}
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default ModalAtualizarCliente;