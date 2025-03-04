import React, { useRef, useState } from "react";
import { DollarSign, User, Phone, Mail, CreditCard, Upload, X, MapPin, Home } from "lucide-react";
import "../css/CadastrarClientes.css"; // Pode manter se tiver outros estilos
import ClientesServices from "../services/ClientesServices";
import { Modal, Button } from "react-bootstrap";

const CadastrarClientes = () => {
    const service = useRef(new ClientesServices()).current;
    const [formData, setFormData] = useState({
        nome: "",
        email: "",
        telefone: "",
        cpf: "",
        limitePagamento: "",
        endereco: "",
        bairro: "",
        cidade: "",
        estado: "",
        numero: ""
    });
    const [foto, setFoto] = useState(null);
    const [previewFoto, setPreviewFoto] = useState(null);
    const [mensagem, setMensagem] = useState("");
    const [sucesso, setSucesso] = useState(false);
    const [modalAberto, setModalAberto] = useState(false);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value,
        });
    };

    const handleFotoChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setFoto(file);
            const reader = new FileReader();
            reader.onloadend = () => {
                setPreviewFoto(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const removerFoto = () => {
        setFoto(null);
        setPreviewFoto(null);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMensagem("");
        setSucesso(false);
        setModalAberto(false);

        try {
            const response = await service.cadastrarCliente(formData, foto);
            setMensagem("Cliente cadastrado com sucesso!");
            setSucesso(true);
            setModalAberto(true);
            setFormData({
                nome: "",
                email: "",
                telefone: "",
                cpf: "",
                limitePagamento: "",
                endereco: "",
                bairro: "",
                cidade: "",
                estado: "",
                numero: ""
            });
            setFoto(null);
            setPreviewFoto(null);
        } catch (error) {
            setMensagem(error.message);
            setSucesso(false);
            setModalAberto(true);
        }
    };

    const fecharModal = () => {
        setModalAberto(false);
        setMensagem("");
    };

    const campos = [
        { id: "nome", label: "Nome Completo", icon: <User size={18} /> },
        { id: "email", label: "E-mail", icon: <Mail size={18} /> },
        { id: "telefone", label: "Telefone", icon: <Phone size={18} /> },
        { id: "cpf", label: "CPF", icon: <CreditCard size={18} /> },
        { id: "limitePagamento", label: "Limite de Pagamento", icon: <DollarSign size={18} /> },
        { id: "endereco", label: "Endereço", icon: <MapPin size={18} /> },
        { id: "bairro", label: "Bairro", icon: <Home size={18} /> },
        { id: "cidade", label: "Cidade", icon: <MapPin size={18} /> },
        { id: "estado", label: "Estado", icon: <MapPin size={18} /> },
        { id: "numero", label: "Número", icon: <Home size={18} /> }
    ];

    return (
        <div className="background-money d-flex justify-content-center align-items-center">
            <div className="form-container">
                <h2 className="form-title">Cadastrar Novo Cliente</h2>

                <form onSubmit={handleSubmit} className="row g-3">
                    <div className="col-12 text-center mb-3">
                        <div className="foto-upload-container">
                            <div className="foto-preview-area">
                                {previewFoto ? (
                                    <div className="foto-preview-wrapper">
                                        <img src={previewFoto} alt="Preview" className="foto-preview" />
                                        <button
                                            type="button"
                                            className="remove-foto-btn"
                                            onClick={removerFoto}
                                            aria-label="Remover foto"
                                        >
                                            <X size={20} />
                                        </button>
                                    </div>
                                ) : (
                                    <div className="foto-upload-placeholder">
                                        <User size={64} className="user-icon" />
                                        <span>Foto do Cliente</span>
                                    </div>
                                )}
                            </div>

                            <label htmlFor="foto-upload" className="foto-upload-btn">
                                <Upload size={16} />
                                <span>Escolher Foto</span>
                                <input
                                    type="file"
                                    id="foto-upload"
                                    accept="image/*"
                                    onChange={handleFotoChange}
                                    className="hidden-input"
                                />
                            </label>
                        </div>
                    </div>

                    {campos.map(({ id, label, icon }, index) => (
                        <div key={index} className="col-md-6">
                            <label htmlFor={id} className="form-label d-flex align-items-center">
                                {icon}
                                <span className="ms-2">{label}</span>
                            </label>
                            <input
                                type="text"
                                className="form-control"
                                id={id}
                                name={id}
                                placeholder={`Digite ${label}`}
                                value={formData[id]}
                                onChange={handleInputChange}
                                required
                            />
                        </div>
                    ))}

                    <div className="col-12 d-flex justify-content-center">
                        <button type="submit" className="btn btn-primary btn-sm">
                            <CreditCard size={16} /> Cadastrar
                        </button>
                    </div>
                </form>

                <p className="privacy-text mt-3">
                    Seus dados estão protegidos de acordo com nossa política de privacidade.
                </p>

                {/* Modal do Bootstrap com suporte a mensagens longas */}
                <Modal show={modalAberto} onHide={fecharModal} centered size="lg"> {/* Aumentei o tamanho para "lg" */}
                    <Modal.Header
                        className={sucesso ? "bg-success text-white" : "bg-danger text-white"}
                        closeButton
                    >
                        <Modal.Title>{sucesso ? "Sucesso" : "Erro"}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body style={{ maxHeight: "400px", overflowY: "auto" }}> {/* Adicionei scroll se necessário */}
                        <p>{mensagem}</p>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={fecharModal}>
                            Fechar
                        </Button>
                    </Modal.Footer>
                </Modal>
            </div>
        </div>
    );
};

export default CadastrarClientes;