<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- Control de stock de insumos y lotes (Encargado de Almacén, Jefe de Producción, Administrador) --%>
<div class="cabecera-pag">
    <div><h1>Stock de insumos y lotes</h1><p>Stock por insumo, alerta de stock mínimo y lotes ordenados por fecha de vencimiento.</p></div>
    <c:if test="${rol == 'ENCARGADO_ALMACEN'}"><div class="acciones"><a class="btn primario" href="${ctx}/insumo">+ Registrar ingreso</a></div></c:if>
</div>
<div class="tarjeta">
    <div class="herramientas">
        <b>Insumos</b>
        <div class="buscador"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>
            <input type="search" placeholder="Buscar insumo..." data-filtrar="tablaInsumos"></div>
    </div>
    <div class="tabla-wrap"><table class="tabla" id="tablaInsumos">
        <thead><tr><th>Insumo</th><th>Tipo</th><th class="num">Stock actual</th><th class="num">Stock mínimo</th><th>Estado</th>
            <c:if test="${rol == 'ENCARGADO_ALMACEN' or rol == 'ADMINISTRADOR'}"><th></th></c:if></tr></thead>
        <tbody>
        <c:forEach var="i" items="${insumos}">
            <tr>
                <td><b><c:out value="${i.nombre}" /></b></td>
                <td>${i.perecible ? 'Perecible' : 'Seco'}</td>
                <td class="num"><fmt:formatNumber value="${i.stockActual}" pattern="#,##0.###" /> ${i.unidadMedida}</td>
                <td class="num"><fmt:formatNumber value="${i.stockMinimo}" pattern="#,##0.###" /> ${i.unidadMedida}</td>
                <td><span class="etiqueta ${i.stockActual < i.stockMinimo ? 'rojo' : 'verde'}">${i.stockActual < i.stockMinimo ? 'Bajo el mínimo' : 'OK'}</span></td>
                <c:if test="${rol == 'ENCARGADO_ALMACEN' or rol == 'ADMINISTRADOR'}">
                    <td><c:if test="${i.stockActual < i.stockMinimo}">
                        <a class="btn chico" href="${ctx}/solicitud?idInsumo=${i.idInsumo}">Solicitar compra</a></c:if></td>
                </c:if>
            </tr>
        </c:forEach>
        </tbody>
    </table></div>
</div>
<div class="tarjeta">
    <div class="herramientas">
        <b>Lotes con saldo (primero vence, primero sale)</b>
        <div class="buscador"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>
            <input type="search" placeholder="Buscar lote..." data-filtrar="tablaLotes"></div>
    </div>
    <div class="tabla-wrap"><table class="tabla" id="tablaLotes">
        <thead><tr><th>Lote</th><th>Insumo</th><th>Proveedor</th><th>Ingreso</th><th>Vencimiento</th><th class="num">Inicial</th><th class="num">Disponible</th><th class="num">Costo unit.</th></tr></thead>
        <tbody>
        <c:forEach var="l" items="${lotes}">
            <tr>
                <td><c:out value="${l.codigo}" /></td>
                <td><c:out value="${l.insumo.nombre}" /></td>
                <td><c:out value="${l.proveedor.razonSocial}" /></td>
                <td>${l.fechaIngreso}</td>
                <td><span class="etiqueta ${l.diasParaVencer < 0 ? 'rojo' : (l.diasParaVencer <= 7 ? 'ambar' : 'verde')}">${l.fechaVencimiento}<c:choose>
                    <c:when test="${l.diasParaVencer < 0}"> · vencido</c:when>
                    <c:when test="${l.diasParaVencer <= 7}"> · ${l.diasParaVencer} días</c:when></c:choose></span></td>
                <td class="num"><fmt:formatNumber value="${l.cantidadInicial}" pattern="#,##0.###" /></td>
                <td class="num"><b><fmt:formatNumber value="${l.cantidadDisponible}" pattern="#,##0.###" /> ${l.insumo.unidadMedida}</b></td>
                <td class="num">S/ <fmt:formatNumber value="${l.costoUnitario}" pattern="#,##0.00" /></td>
            </tr>
        </c:forEach>
        <c:if test="${empty lotes}"><tr class="vacio"><td colspan="8">No hay lotes con saldo.</td></tr></c:if>
        </tbody>
    </table></div>
</div>
<%@ include file="/vista/comun/pie.jspf" %>
