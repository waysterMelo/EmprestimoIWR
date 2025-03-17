// services/ConsultarClienteService.js
import axios from "axios";

const BASE_URL = process.env.REACT_APP_API_URL;

const ConsultarClienteService = {
    buscarClientePorCpf: async (cpf) => {
        return axios.get(`${BASE_URL}/buscar-por-cpf/${cpf}`);
    },


    buscarClienteComEmprestimosPorCpf: async (cpf) => {
        return axios.get(`${BASE_URL}/clientes/buscar-com-emprestimos/${cpf}`);
    },

    atualizarCliente: async (id, clienteDto) => {
        return axios.put(`${BASE_URL}/${id}`, clienteDto);
    },


    buscarFotoClinte: async (cpf) => {
        return axios.get(`${BASE_URL}/clientes/foto/${cpf}`, {responseType: "blob"});
    },


    quitarEmprestimo: async (emprestimoId) => {
        return axios.put(`${BASE_URL}/emprestimo/baixa/${emprestimoId}`);
    },


    pagarParcialmente: async (emprestimoId, valorPago) => {
        const url = `${BASE_URL}/emprestimo/${emprestimoId}/pagar-parcialmente`;
        return axios.put(url, {}, {
            params: { valorPago }
        });
    }


};

export default ConsultarClienteService;
