// services/ConsultarClienteService.js
import axios from "axios";

// Defina a URL base do back-end (ajuste conforme sua configuração)
const BASE_URL = "http://localhost:8080/clientes";

const ConsultarClienteService = {
    buscarClientePorCpf: async (cpf) => {
        return axios.get(`${BASE_URL}/buscar-por-cpf/${cpf}`);
    },


    buscarClienteComEmprestimosPorCpf: async (cpf) => {
        return axios.get(`${BASE_URL}/buscar-com-emprestimos/${cpf}`);
    },

    atualizarCliente: async (id, clienteDto) => {
        return axios.put(`${BASE_URL}/${id}`, clienteDto);
    },


    buscarFotoClinte: async (cpf) => {
        return axios.get(`${BASE_URL}/foto/${cpf}`, {responseType: "blob"});
    },


    quitarEmprestimo: async (emprestimoId) => {
        return axios.put(`${BASE_URL}/emprestimos/baixa/${emprestimoId}`);
    },

    pagarParcialmente: async (emprestimoId, valorPago) => {
        return axios.put(`${BASE_URL}/emprestimos/pagar-parcial`, { emprestimoId, valorPago });
    }


};

export default ConsultarClienteService;
