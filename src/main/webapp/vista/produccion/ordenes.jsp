<%@ include file="/vista/comun/cabecera.jspf" %>
<div class="cabecera-pag">
    <div><h1>Órdenes de producción</h1>
        <p>Al terminar una orden, sus pedidos pasan a LISTO PARA ENTREGA y la tienda puede entregarlos (RN007).</p></div>
    <div class="acciones"><a class="btn primario" href="${ctx}/orden">+ Planificar orden</a></div>
</div>
<c:forEach var="o" items="${ordenes}">
    <div class="tarjeta">
        <div class="titulo">
            <span>Orden N.° ${o.idOrden} · ${o.fechaProduccion} <span class="etiqueta ${o.estado}">${fn:replace(o.estado, '_', ' ')}</span></span>
            <small><c:out value="${o.jefe.nombreCompleto}" /> · ${o.nroPedidos} pedido(s)</small>
        </div>
        <div class="rejilla dos">
            <div class="tabla-wrap"><table class="tabla">
                <thead><tr><th>Producto a elaborar</th><th class="num">Cantidad</th></tr></thead>
                <tbody>
                <c:forEach var="d" items="${o.detalles}">
                    <tr><td><c:out value="${d.nombreProducto}" /></td><td class="num"><b>${d.cantidad}</b></td></tr>
                </c:forEach>
                </tbody>
            </table></div>
            <div>
                <c:if test="${not empty o.observacion}"><p><c:out value="${o.observacion}" /></p></c:if>
                <c:if test="${o.estado == 'EN_PROCESO'}">
                    <form method="post" action="${ctx}/orden" data-confirmar="¿Terminar la orden N.° ${o.idOrden}? Los pedidos pasarán a LISTO PARA ENTREGA.">
                        <input type="hidden" name="accion" value="terminar">
                        <input type="hidden" name="idOrden" value="${o.idOrden}">
                        <button type="submit" class="btn exito">Terminar producción</button>
                    </form>
                </c:if>
                <a class="btn chico" style="margin-top:8px" href="${ctx}/estadoPedido?estado=${o.estado == 'EN_PROCESO' ? 'EN_PRODUCCION' : 'LISTO_ENTREGA'}">Ver pedidos</a>
            </div>
        </div>
    </div>
</c:forEach>
<c:if test="${empty ordenes}"><div class="tarjeta vacio-bloque">Todavía no hay órdenes de producción.</div></c:if>
<%@ include file="/vista/comun/pie.jspf" %>
