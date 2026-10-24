<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- CUS07 - Generar Reporte de Ventas (REQ-12, REQ-13) --%>
<div class="cabecera-pag">
    <div><h1>Reporte de ventas</h1><p>Ventas por tienda y por día en el periodo que elija.</p></div>
    <c:if test="${not empty reporte and not empty reporte.filas}">
        <div class="acciones no-imprimir">
            <button type="button" class="btn" onclick="window.print()">Imprimir</button>
            <a class="btn" href="${ctx}/reporte?tienda=${param.tienda}&desde=${desde}&hasta=${hasta}&formato=csv">Exportar a Excel (CSV)</a>
        </div>
    </c:if>
</div>
<form method="get" action="${ctx}/reporte" class="tarjeta no-imprimir">
    <div class="form">
        <div class="campo c4">
            <label for="tienda">Tienda</label>
            <select name="tienda" id="tienda">
                <option value="0">Todas las tiendas</option>
                <c:forEach var="t" items="${tiendas}">
                    <option value="${t.idTienda}" ${param.tienda == t.idTienda ? 'selected' : ''}><c:out value="${t.nombre}" /></option>
                </c:forEach>
            </select>
        </div>
        <div class="campo"><label for="desde">Desde</label><input type="date" name="desde" id="desde" value="${desde}" required></div>
        <div class="campo"><label for="hasta">Hasta</label><input type="date" name="hasta" id="hasta" value="${hasta}" required></div>
        <div class="campo c2"><button type="submit" class="btn primario ancho">Generar</button></div>
    </div>
</form>

<c:if test="${not empty reporte and not empty reporte.filas}">
    <div class="rejilla kpis">
        <div class="kpi verde"><span>Total vendido</span><b>S/ <fmt:formatNumber value="${reporte.totalGeneral}" pattern="#,##0.00" /></b><small>${desde} al ${hasta}</small></div>
        <c:set var="nVentas" value="0" />
        <c:forEach var="f" items="${reporte.filas}"><c:set var="nVentas" value="${nVentas + f.nroPedidos}" /></c:forEach>
        <div class="kpi"><span>N.° de ventas</span><b>${nVentas}</b><small>comprobantes emitidos</small></div>
        <div class="kpi azul"><span>Ticket promedio</span><b>S/ <fmt:formatNumber value="${reporte.totalGeneral / nVentas}" pattern="#,##0.00" /></b><small>por venta</small></div>
        <div class="kpi"><span>Días con ventas</span><b>${fn:length(reporte.filas)}</b><small>filas del reporte</small></div>
    </div>
    <div class="tabla-wrap"><table class="tabla">
        <thead><tr><th>Fecha</th><th>Tienda</th><th class="num">N.° de ventas</th><th class="num">Total vendido</th></tr></thead>
        <tbody>
        <c:forEach var="f" items="${reporte.filas}">
            <tr>
                <td>${f.fecha}</td>
                <td><c:out value="${f.tienda}" /></td>
                <td class="num">${f.nroPedidos}</td>
                <td class="num">S/ <fmt:formatNumber value="${f.totalVendido}" pattern="#,##0.00" /></td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
            <tr><td colspan="2">Total general</td><td class="num">${nVentas}</td>
                <td class="num">S/ <fmt:formatNumber value="${reporte.totalGeneral}" pattern="#,##0.00" /></td></tr>
        </tfoot>
    </table></div>
</c:if>
<%@ include file="/vista/comun/pie.jspf" %>
