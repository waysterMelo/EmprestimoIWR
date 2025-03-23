import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/Login.css"; // Arquivo de estilos externo

const Login = () => {
    const navigate = useNavigate();
    const [particles, setParticles] = useState([]);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    // Criando partículas animadas de cifrões ($)
    useEffect(() => {
        const createParticles = () => {
            const particlesArray = Array.from({ length: 30 }, (_, i) => ({
                id: i,
                x: Math.random() * 100,
                y: Math.random() * 100,
                size: Math.random() * 20 + 10,
                speed: Math.random() * 0.4 + 0.1,
                opacity: Math.random() * 0.6 + 0.2,
            }));
            setParticles(particlesArray);
        };

        createParticles();

        const interval = setInterval(() => {
            setParticles(prevParticles =>
                prevParticles.map(particle => ({
                    ...particle,
                    y: particle.y > 100 ? -10 : particle.y + particle.speed, // Garante que as partículas reapareçam no topo
                }))
            );
        }, 50);

        return () => clearInterval(interval);
    }, []);

    const handleSubmit = (e) => {
        e.preventDefault();
        // Lógica de autenticação aqui
        // Se autenticação bem-sucedida, redirecionar para Home:
        navigate("/home");
        console.log("Login com:", username, password);
    };

    return (
        <div className="background-money">
            {/* Partículas animadas de cifrões ($) */}
            {particles.map((particle) => (
                <div
                    key={particle.id}
                    className="particle"
                    style={{
                        left: `${particle.x}%`,
                        top: `${particle.y}%`,
                        fontSize: `${particle.size}px`,
                        opacity: particle.opacity,
                    }}
                >
                    $
                </div>
            ))}

            <div className="login-card">
                <h1 className="login-title">Sistema de Empréstimos</h1>
                <p className="login-subtitle">Acesse sua conta para gerenciar empréstimos 💰</p>

                <form className="login-form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="username">Usuário</label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            placeholder="Digite seu usuário"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Senha</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            placeholder="Digite sua senha"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button type="submit" className="btn-login">Entrar</button>
                </form>

                <div className="login-options">
                    <a href="#" onClick={(e) => { e.preventDefault(); /* Lógica para redefinir senha */ }}>
                        Esqueceu a senha?
                    </a>
                    <a href="#" onClick={(e) => { e.preventDefault(); navigate("/cadastro"); }}>
                        Criar nova conta
                    </a>
                </div>
            </div>
        </div>
    );
};

export default Login;