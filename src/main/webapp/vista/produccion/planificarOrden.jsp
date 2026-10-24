<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- CUS04 - Registrar Orden de Producción (RN004, RN008) + CUS05 --%>
<div class="cabecera-pag">
    <div><h1>Planificar orden de producción</h1>
        <p>Seleccione los pedidos REGISTRADOS. El sistema calcula los insumos con la receta estándar (RN004) y los descuenta del almacén.</p></div>
    <div class="acciones"><a class="btn" href="${ctx}/orden?accion=ordenes">Ver órdenes</a><a class="btn" href="${ctx}/stock">Ver stock</a></div>
</div>

<c:if test="${not empty alertas}">
    <div class="alerta aviso"><div><b>Alertas de stock mínimo:</b>
        <ul><c:forEach var="a" items="${alertas}"><li><c:out value="${a}" /></li></c:forEach></ul></div></div>
</c:if>

<c:if test="${not empty faltantes}">
    <div class="tarjeta" style="border-color:#f0c3c0">
        <div class="titulo">Insumos que no alcanzan <small>RN008: debe existir una solicitud de compra para registrar la orden</small></div>
        <div class="tabla-wrap"><table class="tabla">
            <thead><tr><th>Insumo</th><th class="num">Requerido</th><th class="num">Disponible</th><th>Generar solicitud de compra</th></tr></thead>
            <tbody>
            <c:forEach var="f" items="${faltantes}">
                <tr>
                    <td><b><c:out value="${f.insumo.nombre}" /></b></td>
                    <td class="num"><fmt:formatNumber value="${f.requerido}" pattern="#,##0.###" /> ${f.insumo.unidadMedida}</td>
                    <td class="num"><span class="etiqueta rojo"><fmt:formatNumber value="${f.disponible}" pattern="#,##0.###" /> ${f.insumo.unidadMedida}</span></td>
                    <td>
                        <form method="post" action="${ctx}/orden" class="en-linea">
                            <input type="hidden" name="accion" value="solicitud">
                            <input type="hidden" name="idInsumo" value="${f.insumo.idInsumo}">
                            <input type="number" name="cantidad" step="0.001" min="0.001" style="width:130px"
                                   value="<fmt:formatNumber value='${f.cantidadFaltante}' pattern='0.###' />">
                            <button type="submit" class="btn chico">Generar solicitud</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table></div>
    </div>
</c:if>

<form method="post" action="${ctx}/orden" class="tarjeta" data-confirmar="¿Registrar la orden de producción con los pedidos seleccionados? Se descontarán los insumos del almacén.">
    <input type="hidden" name="accion" value="registrar">
    <div class="titulo">Pedidos pendientes de producción <small>${fn:length(pedidos)} pedido(s)</small></div>
    <div class="tabla-wrap"><table class="tabla">
        <thead><tr><th style="width:44px"><input type="checkbox" id="marcarTodos" title="Marcar todos" style="width:20px;min-height:0"></th>
            <th>N.°</th><th>Fecha</th><th>Cliente</th><th>Tienda</th><th>Tipo</th><th>Entrega</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="p" items="${pedidos}">
            <tr>
                <td><input type="checkbox" name="idPedido" value="${p.idPedido}" class="chk" style="width:20px;min-height:0"></td>
                <td><b>${p.idPedido}</b></td>
                <td>${p.fechaRegistroTexto}</td>
                <td><c:out value="${p.cliente.nombres}" /></td>
                <td><c:out value="${p.tienda.nombre}" /></td>
                <td>${p.tipoPedido == 'ESPECIAL' ? '<span class="etiqueta morado">Encargo</span>' : 'Mostrador'}</td>
                <td>${p.fechaEntrega}</td>
                <td><a class="btn chico" href="${ctx}/estadoPedido?accion=ver&id=${p.idPedido}">Ver</a></td>
            </tr>
        </c:forEach>
        <c:if test="${empty pedidos}">
            <tr class="vacio"><td colspan="8">No hay pedidos pendientes de producción.</td></tr>
        </c:if>
        </tbody>
    </table></div>
    <div style="margin-top:16px"><button type="submit" class="btn primario" ${empty pedidos ? 'disabled' : ''}>Registrar orden de producción</button></div>
</form>
<script>
    var todos = document.getElementById('marcarTodos');
    if (todos) {
        todos.addEventListener('change', function () {
            document.querySelectorAll('.chk').forEach(function (c) { c.checked = todos.checked; });
        });
    }
</script>
<%@ include file="/vista/comun/pie.jspf" %>
