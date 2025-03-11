import axios from 'axios';

class EmprestimoServices {


    async realizarEmprestimo(dadosEmprestimo) {
        try {
            const response = await axios.post('/emprestimo', dadosEmprestimo);
            return response.data;
        } catch (error) {
            throw new Error(error.response?.data?.message || 'Erro ao realizar empréstimo.');
        }
    }


}
export default EmprestimoServices;
