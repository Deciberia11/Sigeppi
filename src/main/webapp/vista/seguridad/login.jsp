<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- CUS01 - Iniciar Sesión (REQ-1, REQ-2, REQ-3) --%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>SIGEPPI · Iniciar sesión</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/recursos/css/estilos.css">
</head>
<body>
<div class="fondo-login">
    <section class="login-lado">
        <div>
            <h1>SIGEPPI</h1>
            <p>Sistema de Gestión de Pedidos, Producción e Inventario de la Panadería y Pastelería Delicias del Valle.</p>
        </div>
        <ul>
            <li>Pedidos de las tiendas de San Borja, Surco y La Molina</li>
            <li>Órdenes de producción según la receta estándar</li>
            <li>Control de lotes, vencimientos y stock mínimo</li>
            <li>Reportes de ventas y consumo para la gerencia</li>
        </ul>
        <small>Grupo 02 · Ingeniería de Software 2 · USIL 2026-02</small>
    </section>
    <section class="login-form">
        <div class="caja-login">
            <h2>Iniciar sesión</h2>
            <p class="sub">Ingrese con el usuario que le asignó el administrador.</p>
            <c:if test="${not empty error}">
                <div class="alerta error" role="alert"><c:out value="${error}" /></div>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/login" id="formLogin" novalidate>
                <div class="campo">
                    <label for="usuario">Usuario <span class="req">*</span></label>
                    <input type="text" id="usuario" name="usuario" value="<c:out value='${requestScope.usuario}'/>"
                           autocomplete="username" autofocus>
                </div>
                <div class="campo">
                    <label for="contrasena">Contraseña <span class="req">*</span></label>
                    <input type="password" id="contrasena" name="contrasena" autocomplete="current-password">
                    <label class="check" style="margin-top:6px"><input type="checkbox" id="verClave"> Mostrar contraseña</label>
                </div>
                <button type="submit" class="btn primario ancho">Iniciar sesión</button>
            </form>
        </div>
    </section>
</div>
<script>
    document.getElementById('verClave').addEventListener('change', function () {
        document.getElementById('contrasena').type = this.checked ? 'text' : 'password';
    });
    // REQ-2: aviso rápido en pantalla (el servidor también valida)
    document.getElementById('formLogin').addEventListener('submit', function (e) {
        var u = document.getElementById('usuario').value.trim();
        var c = document.getElementById('contrasena').value;
        if (!u || !c) {
            e.preventDefault();
            alert('Complete los campos obligatorios: usuario y contraseña.');
        }
    });
</script>
</body>
</html>
