<%@ include file="/vista/comun/cabecera.jspf" %>
<c:set var="comp" value="${sessionScope.comprobante}" />
<div class="cabecera-pag no-imprimir">
    <div><h1>Pedido registrado</h1><p>El pedido quedó en estado REGISTRADO y ya lo ve el Jefe de Producción.</p></div>
    <div class="acciones">
        <button type="button" class="btn" onclick="window.print()">Imprimir</button>
        <a class="btn" href="${ctx}/estadoPedido">Pedidos de mi tienda</a>
        <a class="btn primario" href="${ctx}/pedido">+ Otro pedido</a>
    </div>
</div>
<c:choose>
    <c:when test="${empty comp}">
        <div class="alerta info">No hay un comprobante reciente para mostrar.</div>
    </c:when>
    <c:otherwise>
        <div class="tarjeta ticket">
            <p><b>Panadería y Pastelería Delicias del Valle S.A.C.</b><br><c:out value="${u.tienda.nombre}" /></p>
            <p class="codigo"><c:out value="${comp.tipo}" /> <c:out value="${comp.serie}" />-<fmt:formatNumber value="${comp.numero}" pattern="00000000" /></p>
            <p>Pedido N.° <b>${comp.idPedido}</b> · Pago: <b><c:out value="${comp.metodoPago}" /></b></p>
            <p class="monto-grande">S/ <fmt:formatNumber value="${comp.total}" pattern="#,##0.00" /></p>
            <a class="btn no-imprimir" href="${ctx}/estadoPedido?accion=ver&id=${comp.idPedido}">Ver detalle del pedido</a>
            <p class="ayuda">Comprobante interno. La facturación electrónica SUNAT no está en esta etapa.</p>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="/vista/comun/pie.jspf" %>
