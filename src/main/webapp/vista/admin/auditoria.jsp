<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- RNF-03: bitácora de acciones críticas con usuario, fecha, hora, IP y acción --%>
<div class="cabecera-pag">
    <div><h1>Auditoría</h1><p>Últimas 300 acciones registradas: inicios de sesión, pedidos, órdenes, ingresos de stock y cambios de administración (RNF-03).</p></div>
</div>
<form method="get" action="${ctx}/admin" class="tarjeta">
    <input type="hidden" name="entidad" value="auditoria">
    <div class="form">
        <div class="campo c4"><label>Módulo</label>
            <select name="modulo">
                <option value="">Todos</option>
                <c:forEach var="m" items="${['SEGURIDAD','PEDIDOS','PRODUCCION','INVENTARIO','ADMINISTRACION']}">
                    <option value="${m}" ${param.modulo == m ? 'selected' : ''}>${m}</option>
                </c:forEach>
            </select></div>
        <div class="campo c6"><label>Buscar (acción, detalle o usuario)</label><input name="q" value="<c:out value='${param.q}'/>" placeholder="Ej. LOGIN_FALLIDO"></div>
        <div class="campo c2"><button type="submit" class="btn primario ancho">Filtrar</button></div>
    </div>
</form>
<div class="tabla-wrap"><table class="tabla">
    <thead><tr><th>Fecha y hora</th><th>Usuario</th><th>IP</th><th>Módulo</th><th>Acción</th><th>Detalle</th></tr></thead>
    <tbody>
    <c:forEach var="a" items="${lista}">
        <tr><td>${a.fechaTexto}</td><td><c:out value="${a.usuario}" /></td><td><c:out value="${a.ip}" /></td><td>${a.modulo}</td>
            <td><span class="etiqueta ${fn:contains(a.accion, 'FALLIDO') or fn:contains(a.accion, 'DESACTIVADO') ? 'rojo' : 'azul'}"><c:out value="${a.accion}" /></span></td>
            <td><c:out value="${a.detalle}" /></td></tr>
    </c:forEach>
    <c:if test="${empty lista}"><tr class="vacio"><td colspan="6">No hay registros con ese filtro.</td></tr></c:if>
    </tbody>
</table></div>
<%@ include file="/vista/comun/pie.jspf" %>
