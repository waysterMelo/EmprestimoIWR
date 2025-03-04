import React, { useRef, useState } from "react";
import { DollarSign, User, Phone, Mail, CreditCard, Upload, X, MapPin, Home } from "lucide-react";
import "../css/CadastrarClientes.css";
import ClientesServices from "../services/ClientesServices";

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

        if (!validarCPF(formData.cpf)) {
            setMensagem("CPF inválido!");
            setSucesso(false);
            return;
        }

        try {
            const response = await service.cadastrarCliente(formData, foto);
            setMensagem("Cliente cadastrado com sucesso!");
            setSucesso(true);
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
        }
    };

    const validarCPF = (cpf) => {
        if (!cpf) return false;
        cpf = cpf.replace(/[^\d]/g, "");
        if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false;

        let soma = 0;
        for (let i = 0; i < 9; i++) {
            soma += parseInt(cpf.charAt(i)) * (10 - i);
        }
        let resto = soma % 11;
        let digito1 = resto < 2 ? 0 : 11 - resto;

        soma = 0;
        for (let i = 0; i < 10; i++) {
            soma += parseInt(cpf.charAt(i)) * (11 - i);
        }
        resto = soma % 11;
        let digito2 = resto < 2 ? 0 : 11 - resto;

        return digito1 === parseInt(cpf.charAt(9)) && digito2 === parseInt(cpf.charAt(10));
    };

    return (
        <div className="background-money">
            <div className="form-container">
                <h2 className="form-title">Cadastrar Novo Cliente</h2>

                {mensagem && (
                    <div className={`message ${sucesso ? "success" : "error"}`}>
                        {mensagem}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
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

                    <div className="input-grid">
                        {[
                            { id: "nome", label: "Nome Completo", icon: <User size={18} />, placeholder: "Digite o nome completo" },
                            { id: "email", label: "E-mail", icon: <Mail size={18} />, placeholder: "email@exemplo.com", type: "email" },
                            { id: "telefone", label: "Telefone", icon: <Phone size={18} />, placeholder: "(99) 99999-9999", type: "tel" },
                            { id: "cpf", label: "CPF", icon: <User size={18} />, placeholder: "000.000.000-00" },
                            { id: "limitePagamento", label: "Limite de Pagamento", icon: <DollarSign size={18} />, placeholder: "R$ 0,00", type: "number" },
                            { id: "endereco", label: "Endereço", icon: <MapPin size={18} />, placeholder: "Rua, Avenida, etc." },
                            { id: "bairro", label: "Bairro", icon: <Home size={18} />, placeholder: "Digite o bairro" },
                            { id: "cidade", label: "Cidade", icon: <MapPin size={18} />, placeholder: "Digite a cidade" },
                            { id: "estado", label: "Estado", icon: <MapPin size={18} />, placeholder: "Digite o estado" },
                            { id: "numero", label: "Número", icon: <Home size={18} />, placeholder: "Número da residência" }
                        ].map(({ id, label, icon, placeholder, type = "text" }) => (
                            <div className="input-group" key={id}>
                                <label htmlFor={id}>
                                    {icon}
                                    <span>{label}</span>
                                </label>
                                <input
                                    type={type}
                                    id={id}
                                    name={id}
                                    value={formData[id]}
                                    onChange={handleInputChange}
                                    placeholder={placeholder}
                                    required
                                />
                            </div>
                        ))}
                    </div>

                    <button type="submit" className="submit-btn">
                        <CreditCard size={18} />
                        <span>Cadastrar Cliente</span>
                    </button>

                    <p className="privacy-text">
                        Seus dados estão protegidos de acordo com nossa política de privacidade.
                    </p>
                </form>
            </div>
        </div>
    );
};

export default CadastrarClientes;
