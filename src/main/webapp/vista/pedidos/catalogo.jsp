<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- Consulta de disponibilidad de productos (funcionalidad del Vendedor en la ES) --%>
<div class="cabecera-pag">
    <div><h1>Catálogo de productos</h1><p>Productos disponibles para la venta, con su precio y tiempo de preparación.</p></div>
    <c:if test="${rol == 'VENDEDOR'}"><div class="acciones"><a class="btn primario" href="${ctx}/pedido">+ Registrar pedido</a></div></c:if>
</div>
<div class="herramientas">
    <div class="buscador"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>
        <input type="search" placeholder="Buscar producto..." data-filtrar="tablaCatalogo"></div>
    <span class="ayuda">${fn:length(productos)} producto(s) disponibles</span>
</div>
<div class="tabla-wrap"><table class="tabla" id="tablaCatalogo">
    <thead><tr><th>Producto</th><th>Descripción</th><th>Tipo</th><th class="num">Preparación</th><th class="num">Precio (sin IGV)</th><th class="num">Precio con IGV</th></tr></thead>
    <tbody>
    <c:forEach var="p" items="${productos}">
        <tr>
            <td><b><c:out value="${p.nombre}" /></b></td>
            <td><c:out value="${p.descripcion}" /></td>
            <td><span class="etiqueta ${p.tipoProducto == 'ESPECIAL' ? 'morado' : 'azul'}">${p.tipoProducto == 'ESPECIAL' ? 'Por encargo' : 'Mostrador'}</span></td>
            <td class="num">${p.tiempoPreparacion} min</td>
            <td class="num">S/ <fmt:formatNumber value="${p.precioUnitario}" pattern="#,##0.00" /></td>
            <td class="num"><b>S/ <fmt:formatNumber value="${p.precioUnitario * 1.18}" pattern="#,##0.00" /></b></td>
        </tr>
    </c:forEach>
    <c:if test="${empty productos}"><tr class="vacio"><td colspan="6">No hay productos activos.</td></tr></c:if>
    </tbody>
</table></div>
<p class="ayuda">Los encargos especiales se registran en "Registrar pedido" con tipo "Especial" y fecha de entrega.</p>
<%@ include file="/vista/comun/pie.jspf" %>
