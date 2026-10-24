<%@ include file="/vista/comun/cabecera.jspf" %>
<div class="cabecera-pag">
    <div><h1>Pedido N.° ${pedido.idPedido}</h1>
        <p><c:out value="${pedido.tienda.nombre}" /> · registrado el ${pedido.fechaRegistroTexto}</p></div>
    <div class="acciones no-imprimir">
        <button type="button" class="btn" onclick="window.print()">Imprimir</button>
        <a class="btn" href="${ctx}/estadoPedido">Volver a la lista</a>
    </div>
</div>
<div class="rejilla dos">
    <div class="tarjeta">
        <div class="titulo">Datos del pedido <span class="etiqueta ${pedido.estado}">${fn:replace(pedido.estado, '_', ' ')}</span></div>
        <table class="tabla">
            <tr><td>Cliente</td><td><b><c:out value="${pedido.cliente.nombres}" /></b></td></tr>
            <tr><td>Tipo</td><td>${pedido.tipoPedido == 'ESPECIAL' ? 'Encargo especial' : 'Mostrador'}</td></tr>
            <tr><td>Fecha de entrega</td><td>${empty pedido.fechaEntrega ? '—' : pedido.fechaEntrega}</td></tr>
        </table>
    </div>
    <div class="tarjeta">
        <div class="titulo">Historial de estados <small>trazabilidad (RNF-03)</small></div>
        <ul class="linea-tiempo">
            <c:forEach var="h" items="${historial}">
                <li><b>${fn:replace(h.estadoNuevo, '_', ' ')}</b>
                    <small>${h.fechaTexto} · <c:out value="${h.usuario}" /></small></li>
            </c:forEach>
            <c:if test="${empty historial}"><li>Sin historial registrado.</li></c:if>
        </ul>
    </div>
</div>
<div class="tarjeta" style="margin-top:20px">
    <h3>Detalle</h3>
    <div class="tabla-wrap"><table class="tabla">
        <thead><tr><th>Producto</th><th class="num">Cantidad</th><th class="num">Precio unit.</th><th class="num">Subtotal</th></tr></thead>
        <tbody>
        <c:forEach var="d" items="${pedido.detalles}">
            <tr><td><c:out value="${d.producto.nombre}" /></td><td class="num">${d.cantidad}</td>
                <td class="num">S/ <fmt:formatNumber value="${d.precioUnitario}" pattern="#,##0.00" /></td>
                <td class="num">S/ <fmt:formatNumber value="${d.subtotal}" pattern="#,##0.00" /></td></tr>
        </c:forEach>
        </tbody>
        <tfoot>
            <tr><td colspan="3" class="num">Subtotal</td><td class="num">S/ <fmt:formatNumber value="${pedido.subtotal}" pattern="#,##0.00" /></td></tr>
            <tr><td colspan="3" class="num">IGV (18 %)</td><td class="num">S/ <fmt:formatNumber value="${pedido.igv}" pattern="#,##0.00" /></td></tr>
            <tr><td colspan="3" class="num">Total</td><td class="num">S/ <fmt:formatNumber value="${pedido.total}" pattern="#,##0.00" /></td></tr>
        </tfoot>
    </table></div>
</div>
<%@ include file="/vista/comun/pie.jspf" %>
