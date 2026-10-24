<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- Gestión de abastecimiento: solicitudes de compra con contacto del proveedor (ES 5.3.1) --%>
<div class="cabecera-pag">
    <div><h1>Solicitudes de compra</h1><p>Pedidos internos de compra cuando un insumo falta o baja del stock mínimo. Al registrar el ingreso del lote se marcan como atendidas.</p></div>
</div>
<form method="post" action="${ctx}/solicitud" class="tarjeta">
    <input type="hidden" name="accion" value="crear">
    <h3>Nueva solicitud</h3>
    <div class="form">
        <div class="campo c4">
            <label for="idInsumo">Insumo <span class="req">*</span></label>
            <select name="idInsumo" id="idInsumo">
                <c:forEach var="i" items="${insumos}">
                    <option value="${i.idInsumo}" ${param.idInsumo == i.idInsumo ? 'selected' : ''}><c:out value="${i.nombre}" /> · stock <fmt:formatNumber value="${i.stockActual}" pattern="#,##0.###" /> ${i.unidadMedida}</option>
                </c:forEach>
            </select>
        </div>
        <div class="campo c4">
            <label for="idProveedor">Proveedor sugerido</label>
            <select name="idProveedor" id="idProveedor">
                <option value="0">— Sin proveedor —</option>
                <c:forEach var="pv" items="${proveedores}">
                    <option value="${pv.idProveedor}"><c:out value="${pv.razonSocial}" /></option>
                </c:forEach>
            </select>
        </div>
        <div class="campo c2">
            <label for="cantidad">Cantidad <span class="req">*</span></label>
            <input type="number" name="cantidad" id="cantidad" step="0.001" min="0.001" required>
        </div>
        <div class="campo c2"><button type="submit" class="btn primario ancho">Registrar</button></div>
        <div class="campo c12">
            <label for="motivo">Motivo</label>
            <input type="text" name="motivo" id="motivo" maxlength="150" placeholder="Ej. stock por debajo del mínimo, campaña de Navidad">
        </div>
    </div>
</form>

<div class="pestanas">
    <a href="${ctx}/solicitud" class="${empty param.estado ? 'activo' : ''}">Todas</a>
    <a href="${ctx}/solicitud?estado=PENDIENTE" class="${param.estado == 'PENDIENTE' ? 'activo' : ''}">Pendientes</a>
    <a href="${ctx}/solicitud?estado=ATENDIDA" class="${param.estado == 'ATENDIDA' ? 'activo' : ''}">Atendidas</a>
    <a href="${ctx}/solicitud?estado=ANULADA" class="${param.estado == 'ANULADA' ? 'activo' : ''}">Anuladas</a>
</div>
<div class="tabla-wrap"><table class="tabla">
    <thead><tr><th>N.°</th><th>Fecha</th><th>Insumo</th><th class="num">Cantidad</th><th>Proveedor / contacto</th><th>Motivo</th><th>Estado</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="s" items="${solicitudes}">
        <tr>
            <td>${s.idSolicitud}</td>
            <td>${s.fechaTexto}<br><small class="ayuda"><c:out value="${s.nombreUsuario}" /></small></td>
            <td><b><c:out value="${s.nombreInsumo}" /></b></td>
            <td class="num"><fmt:formatNumber value="${s.cantidad}" pattern="#,##0.###" /> ${s.unidad}</td>
            <td><c:choose>
                <c:when test="${empty s.nombreProveedor}"><span class="ayuda">Sin proveedor</span></c:when>
                <c:otherwise><c:out value="${s.nombreProveedor}" /><br>
                    <small class="ayuda"><c:out value="${s.telefonoProveedor}" />
                        <c:if test="${not empty s.correoProveedor}"> · <a href="mailto:${s.correoProveedor}?subject=Solicitud%20de%20compra%20N.%C2%B0%20${s.idSolicitud}%20-%20Delicias%20del%20Valle"><c:out value="${s.correoProveedor}" /></a></c:if></small>
                </c:otherwise></c:choose></td>
            <td><c:out value="${s.motivo}" /></td>
            <td><span class="etiqueta ${s.estado}">${s.estado}</span></td>
            <td><c:if test="${s.estado == 'PENDIENTE'}">
                <form method="post" action="${ctx}/solicitud" data-confirmar="¿Anular la solicitud N.° ${s.idSolicitud}?">
                    <input type="hidden" name="accion" value="ANULADA">
                    <input type="hidden" name="idSolicitud" value="${s.idSolicitud}">
                    <button type="submit" class="btn chico peligro">Anular</button>
                </form></c:if></td>
        </tr>
    </c:forEach>
    <c:if test="${empty solicitudes}"><tr class="vacio"><td colspan="8">No hay solicitudes con ese filtro.</td></tr></c:if>
    </tbody>
</table></div>
<%@ include file="/vista/comun/pie.jspf" %>
