<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- CUS03 - Actualizar Estado de Pedido (RN007, RN008) --%>
<div class="cabecera-pag">
    <div><h1>${rol == 'VENDEDOR' ? 'Pedidos de mi tienda' : 'Pedidos de las tiendas'}</h1>
        <p>Los estados avanzan en orden: Registrado → En producción → Listo para entrega → Entregado (RN007).</p></div>
    <c:if test="${rol == 'VENDEDOR'}"><div class="acciones"><a class="btn primario" href="${ctx}/pedido">+ Registrar pedido</a></div></c:if>
</div>
<div class="pestanas">
    <a href="${ctx}/estadoPedido" class="${empty param.estado ? 'activo' : ''}">Todos</a>
    <c:forEach var="e" items="${['REGISTRADO','EN_PRODUCCION','LISTO_ENTREGA','ENTREGADO']}">
        <a href="${ctx}/estadoPedido?estado=${e}" class="${param.estado == e ? 'activo' : ''}">${fn:replace(e, '_', ' ')}</a>
    </c:forEach>
</div>
<div class="herramientas">
    <div class="buscador"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>
        <input type="search" placeholder="Buscar por cliente, N.°..." data-filtrar="tablaPedidos"></div>
    <span class="ayuda">${fn:length(pedidos)} pedido(s)</span>
</div>
<div class="tabla-wrap"><table class="tabla" id="tablaPedidos">
    <thead>
    <tr><th>N.°</th><th>Fecha</th><th>Cliente</th><c:if test="${rol != 'VENDEDOR'}"><th>Tienda</th></c:if><th>Tipo</th><th>Entrega</th><th class="num">Total</th><th>Estado</th><th>Acciones</th></tr>
    </thead>
    <tbody>
    <c:forEach var="p" items="${pedidos}">
        <tr>
            <td><b>${p.idPedido}</b></td>
            <td>${p.fechaRegistroTexto}</td>
            <td><c:out value="${p.cliente.nombres}" /></td>
            <c:if test="${rol != 'VENDEDOR'}"><td><c:out value="${p.tienda.nombre}" /></td></c:if>
            <td>${p.tipoPedido == 'ESPECIAL' ? 'Encargo' : 'Mostrador'}</td>
            <td>${p.fechaEntrega}</td>
            <td class="num">S/ <fmt:formatNumber value="${p.total}" pattern="#,##0.00" /></td>
            <td><span class="etiqueta ${p.estado}">${fn:replace(p.estado, '_', ' ')}</span></td>
            <td class="en-linea">
                <a class="btn chico" href="${ctx}/estadoPedido?accion=ver&id=${p.idPedido}">Ver</a>
                <c:if test="${(rol == 'JEFE_PRODUCCION' and p.estado == 'EN_PRODUCCION') or (rol == 'VENDEDOR' and p.estado == 'LISTO_ENTREGA')}">
                    <form method="post" action="${ctx}/estadoPedido"
                          data-confirmar="${p.estado == 'EN_PRODUCCION' ? '¿Marcar el pedido como LISTO PARA ENTREGA?' : '¿Confirmar que el cliente recibió el pedido?'}">
                        <input type="hidden" name="idPedido" value="${p.idPedido}">
                        <button type="submit" class="btn chico exito">${p.estado == 'EN_PRODUCCION' ? 'Marcar listo' : 'Marcar entregado'}</button>
                    </form>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty pedidos}">
        <tr class="vacio"><td colspan="9">No hay pedidos con ese filtro.</td></tr>
    </c:if>
    </tbody>
</table></div>
<%@ include file="/vista/comun/pie.jspf" %>
