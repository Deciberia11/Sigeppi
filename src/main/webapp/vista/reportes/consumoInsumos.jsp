<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- Análisis de consumo histórico de insumos (PAQ05 - Reportes) --%>
<div class="cabecera-pag">
    <div><h1>Consumo histórico de insumos</h1><p>Cuánto se consumió de cada insumo por las órdenes de producción. Sirve para planificar las compras y reducir mermas (ON-02).</p></div>
    <c:if test="${not empty filas}">
        <div class="acciones no-imprimir">
            <button type="button" class="btn" onclick="window.print()">Imprimir</button>
            <a class="btn" href="${ctx}/reporte?tipo=consumo&desde=${desde}&hasta=${hasta}&formato=csv">Exportar a Excel (CSV)</a>
        </div>
    </c:if>
</div>
<form method="get" action="${ctx}/reporte" class="tarjeta no-imprimir">
    <input type="hidden" name="tipo" value="consumo">
    <div class="form">
        <div class="campo c4"><label for="desde">Desde</label><input type="date" name="desde" id="desde" value="${desde}" required></div>
        <div class="campo c4"><label for="hasta">Hasta</label><input type="date" name="hasta" id="hasta" value="${hasta}" required></div>
        <div class="campo c2"><button type="submit" class="btn primario ancho">Consultar</button></div>
    </div>
</form>
<c:if test="${not empty filas}">
    <div class="tarjeta">
        <div class="titulo">Consumo por insumo <small>${desde} al ${hasta}</small></div>
        <div class="barras" style="margin-bottom:20px">
            <c:forEach var="c" items="${filas}">
                <div class="barra-fila"><span><c:out value="${c.insumo}" /></span>
                    <div class="pista"><div class="relleno" style="width:${c.porcentaje}%"></div></div>
                    <span class="valor"><fmt:formatNumber value="${c.cantidad}" pattern="#,##0.###" /> ${c.unidad}</span></div>
            </c:forEach>
        </div>
        <div class="tabla-wrap"><table class="tabla">
            <thead><tr><th>Insumo</th><th class="num">Cantidad consumida</th><th>Unidad</th><th class="num">N.° de órdenes</th></tr></thead>
            <tbody>
            <c:forEach var="c" items="${filas}">
                <tr><td><c:out value="${c.insumo}" /></td><td class="num"><fmt:formatNumber value="${c.cantidad}" pattern="#,##0.###" /></td>
                    <td>${c.unidad}</td><td class="num">${c.nroOrdenes}</td></tr>
            </c:forEach>
            </tbody>
        </table></div>
    </div>
</c:if>
<%@ include file="/vista/comun/pie.jspf" %>
