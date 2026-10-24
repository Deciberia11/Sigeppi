<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>SIGEPPI · Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/recursos/css/estilos.css">
</head>
<body>
<div class="login-form" style="min-height:100vh">
    <div class="caja-login tarjeta">
        <c:choose>
            <c:when test="${requestScope['jakarta.servlet.error.status_code'] == 403}">
                <h2>Acceso no permitido</h2>
                <p class="sub">Su rol no tiene permiso para entrar a este módulo.</p>
            </c:when>
            <c:otherwise>
                <h2>Ocurrió un problema</h2>
                <p class="sub">No se pudo completar la operación. Si el error continúa, revise que la base de datos esté
                    disponible y que los datos de <b>db.properties</b> sean correctos.</p>
                <p class="detalle"><c:out value="${requestScope['jakarta.servlet.error.message']}" /></p>
            </c:otherwise>
        </c:choose>
        <a class="btn primario ancho" href="${pageContext.request.contextPath}/inicio">Volver al inicio</a>
    </div>
</div>
</body>
</html>
