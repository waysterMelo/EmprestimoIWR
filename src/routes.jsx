import React from 'react';
import { Routes, Route } from 'react-router-dom';
import CadastrarEmprestimo from './app/components/CadastrarEmprestimo';
import BuscarClientePorCpf from './app/components/BuscarClientePorCpf';
import PagarParcialmente from './app/components/PagarParcialmente';
import RealizarBaixaPagamento from './app/components/RealizarBaixaPagamento';
import EmprestimosVencidosHoje from './app/components/EmprestimosVencidosHoje';
import Home from "./Home";
import CadastrarClientes from "./app/components/CadastrarClientes";

function RoutesApp(){
    return (
        <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/" element={<h1>Bem-vindo ao Sistema de Empréstimos</h1>} />
            <Route path={'/cadastrar'} element={ <CadastrarEmprestimo/> }/>
            <Route path="/buscar" element={<BuscarClientePorCpf />} />
            <Route path="/pagar-parcial" element={<PagarParcialmente />} />
            <Route path="/baixa-pagamento" element={<RealizarBaixaPagamento />} />
            <Route path="/vencidos-hoje" element={<EmprestimosVencidosHoje />} />
            <Route path="/cadastrar-cliente" element={<CadastrarClientes />} />
            <Route path="*" element={<h1>Página não encontrada</h1>} />
        </Routes>
    )
}

export default RoutesApp;