import axios from "axios";

const api_url = process.env.REACT_APP_API_URL;

if (!api_url) {
    throw new Error("A URL base da API não foi fornecida e REACT_APP_API_URL não está definida.");
}

class ClientesServices {
    constructor(baseUrl = api_url) {
        this.API_URL = baseUrl;
    }

    #criarFormData(dados, foto) {
        const formData = new FormData();
        Object.entries(dados).forEach(([key, value]) => {
            if (value !== null && value !== undefined) {
                formData.append(key, value.toString()); // Converte valores numéricos e booleanos para string
            }
        });
        if (foto) {
            formData.append("foto", foto);
        }
        return formData;
    }


    async cadastrarCliente(dados, foto) {
        try {
            // Usa sempre FormData, pois o endpoint espera multipart/form-data
            const formData = new FormData();

            // Adiciona todos os campos ao FormData
            Object.entries(dados).forEach(([key, value]) => {
                if (value !== null && value !== undefined) {
                    formData.append(key, value.toString());
                }
            });

            // Adiciona a foto, se existir
            if (foto) {
                formData.append("foto", foto);
            }

            // Realiza a requisição sem definir manualmente o Content-Type
            const response = await axios.post(`${this.API_URL}/clientes`, formData, {
                headers: {
                    // Deixe o axios definir automaticamente o Content-Type
                },
            });

            return response.data;
        } catch (error) {
            const errorMessage = error.response
                ? `Erro ${error.response.status}: ${error.response.data.message || error.message}`
                : `Erro ao conectar com a API: ${error.message}`;
            console.error("Erro ao cadastrar cliente:", errorMessage);
            throw new Error(errorMessage);
        }
    }


}

export default ClientesServices;