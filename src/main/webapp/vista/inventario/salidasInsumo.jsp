<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- CUS05 - Registrar Salida de Insumos por Producción (consulta del kardex) --%>
<div class="cabecera-pag">
    <div><h1>Salidas de insumos por producción</h1><p>Movimientos de los últimos 7 días, descontados por lote: primero vence, primero sale (FEFO).</p></div>
</div>
<div class="herramientas">
    <div class="buscador"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>
        <input type="search" placeholder="Buscar..." data-filtrar="tablaSalidas"></div>
</div>
<div class="tabla-wrap"><table class="tabla" id="tablaSalidas">
    <thead><tr><th>Fecha</th><th>Lote</th><th>Orden</th><th class="num">Cantidad</th><th>Motivo</th></tr></thead>
    <tbody>
    <c:forEach var="m" items="${salidas}">
        <tr>
            <td>${m.fechaTexto}</td>
            <td>${m.idLote}</td>
            <td>${empty m.idOrden ? '—' : m.idOrden}</td>
            <td class="num"><fmt:formatNumber value="${m.cantidad}" pattern="#,##0.###" /></td>
            <td><c:out value="${m.motivo}" /></td>
        </tr>
    </c:forEach>
    <c:if test="${empty salidas}">
        <tr class="vacio"><td colspan="5">No hay salidas registradas en los últimos 7 días.</td></tr>
    </c:if>
    </tbody>
</table></div>
<%@ include file="/vista/comun/pie.jspf" %>
