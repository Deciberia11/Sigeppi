<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- CUS02 - Registrar Pedido (RN001, RN002, RN003, RN005) --%>
<div class="cabecera-pag">
    <div><h1>Registrar pedido</h1><p><c:out value="${u.tienda.nombre}" /> · el total se calcula con IGV del 18 % (RN003).</p></div>
    <div class="acciones"><a class="btn" href="${ctx}/catalogo">Ver catálogo</a></div>
</div>
<form method="post" action="${ctx}/pedido" id="formPedido">
    <div class="rejilla dos">
        <div class="tarjeta">
            <h3>1. Cliente</h3>
            <div class="form">
                <div class="campo c12">
                    <label for="idCliente">Cliente <span class="req">*</span></label>
                    <select name="idCliente" id="idCliente">
                        <c:forEach var="cl" items="${clientes}">
                            <option value="${cl.idCliente}" ${cl.tipoCliente == 'GENERICO' ? 'selected' : ''}>
                                <c:out value="${cl.nombres}" /><c:if test="${not empty cl.telefono}"> · <c:out value="${cl.telefono}" /></c:if>
                            </option>
                        </c:forEach>
                        <option value="">+ Registrar cliente nuevo</option>
                    </select>
                    <span class="ayuda">Para ventas de mostrador deje el cliente genérico (RN001).</span>
                </div>
                <div class="campo c6 nuevo-cliente oculto">
                    <label for="nombreCliente">Nombre del cliente nuevo <span class="req">*</span></label>
                    <input type="text" name="nombreCliente" id="nombreCliente" maxlength="100">
                </div>
                <div class="campo c6 nuevo-cliente oculto">
                    <label for="telefonoCliente">Teléfono</label>
                    <input type="tel" name="telefonoCliente" id="telefonoCliente" maxlength="15">
                </div>
            </div>
        </div>
        <div class="tarjeta">
            <h3>2. Tipo de pedido</h3>
            <div class="form">
                <div class="campo c6">
                    <label for="tipoPedido">Tipo</label>
                    <select name="tipoPedido" id="tipoPedido">
                        <option value="MOSTRADOR">Mostrador</option>
                        <option value="ESPECIAL">Especial (por encargo)</option>
                    </select>
                </div>
                <div class="campo c6">
                    <label for="fechaEntrega">Fecha de entrega</label>
                    <input type="date" name="fechaEntrega" id="fechaEntrega">
                </div>
                <div class="campo c12">
                    <label for="observaciones">Observaciones (dedicatoria, sabor, decoración...)</label>
                    <input type="text" name="observaciones" id="observaciones" maxlength="250">
                </div>
            </div>
        </div>
    </div>

    <div class="tarjeta" style="margin-top:20px">
        <h3>3. Productos</h3>
        <div class="form">
            <div class="campo c8">
                <label for="selProducto">Producto</label>
                <select id="selProducto">
                    <c:forEach var="pr" items="${productos}">
                        <option value="${pr.idProducto}" data-precio="${pr.precioUnitario}" data-nombre="<c:out value='${pr.nombre}'/>">
                            <c:out value="${pr.nombre}" /> - S/ <fmt:formatNumber value="${pr.precioUnitario}" pattern="0.00" />
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="campo c2">
                <label for="txtCantidad">Cantidad</label>
                <input type="number" id="txtCantidad" min="1" step="1" value="1">
            </div>
            <div class="campo c2"><button type="button" class="btn ancho" id="btnAgregar">Agregar</button></div>
        </div>
        <div class="tabla-wrap" style="margin-top:16px"><table class="tabla">
            <thead><tr><th>Producto</th><th class="num">Cantidad</th><th class="num">Precio unit.</th><th class="num">Subtotal</th><th></th></tr></thead>
            <tbody id="cuerpoDetalle">
                <tr class="vacio"><td colspan="5">Todavía no hay productos en el pedido.</td></tr>
            </tbody>
        </table></div>
        <div class="totales">
            <div>Subtotal: <b>S/ <span id="lblSubtotal">0.00</span></b></div>
            <div>IGV (18 %): <b>S/ <span id="lblIgv">0.00</span></b></div>
            <div class="total">Total: S/ <span id="lblTotal">0.00</span></div>
        </div>
    </div>

    <div class="tarjeta">
        <h3>4. Pago</h3>
        <div class="form">
            <div class="campo c4">
                <label for="metodoPago">Método de pago</label>
                <select name="metodoPago" id="metodoPago">
                    <option value="EFECTIVO">Efectivo</option>
                    <option value="TARJETA">Tarjeta</option>
                    <option value="YAPE">Yape</option>
                    <option value="PLIN">Plin</option>
                </select>
            </div>
            <div class="campo c8" style="align-items:flex-end">
                <button type="submit" class="btn primario">Confirmar pedido y emitir boleta</button>
            </div>
        </div>
    </div>
</form>
<script src="${ctx}/recursos/js/pedido.js"></script>
<%@ include file="/vista/comun/pie.jspf" %>
