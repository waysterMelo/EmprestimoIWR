import React from 'react';
import { Routes, Route } from 'react-router-dom';
import CadastrarEmprestimo from './app/components/RealizarEmprestimo';
import EmprestimosVencidosHoje from './app/components/EmprestimosVencidosHoje';
import Home from "./Home";
import CadastrarClientes from "./app/components/CadastrarClientes";
import ConsultarCliente from "./app/components/ConsultarCliente";
import Login from "./app/components/Login";

function RoutesApp(){
    return (
        <Routes>
            <Route path="/" element={<Login />} />
            <Route path="/home" element={<Home />} />
            <Route path={'/realizar-emprestimo'} element={ <CadastrarEmprestimo/> }/>
            <Route path="/consultar-cliente" element={<ConsultarCliente />} />
            <Route path="/vencidos-hoje" element={<EmprestimosVencidosHoje />} />
            <Route path="/cadastrar-cliente" element={<CadastrarClientes />} />
            <Route path="*" element={<h1>Página não encontrada</h1>} />
        </Routes>
    )
}

export default RoutesApp;