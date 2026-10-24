<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- Productos y receta estándar (RN004, RN005, RNF-10) --%>
<div class="cabecera-pag">
    <div><h1>Productos y recetas</h1><p>Catálogo de productos y su receta estándar: insumos por UNA unidad de producto (RN004).</p></div>
    <div class="acciones"><a class="btn" href="${ctx}/admin?entidad=productos">+ Nuevo producto</a></div>
</div>
<div class="rejilla ${empty editar ? '' : 'dos'}" style="${empty editar ? '' : 'margin-bottom:20px'}">
    <form method="post" action="${ctx}/admin" class="tarjeta" id="formProducto">
        <input type="hidden" name="entidad" value="productos">
        <input type="hidden" name="accion" value="guardar">
        <input type="hidden" name="id" value="${empty editar ? 0 : editar.idProducto}">
        <h3>${empty editar ? 'Nuevo producto' : 'Editar producto'}</h3>
        <div class="form">
            <div class="campo c6"><label>Nombre <span class="req">*</span></label><input name="nombre" maxlength="80" required value="<c:out value='${editar.nombre}'/>"></div>
            <div class="campo c6"><label>Tipo</label>
                <select name="tipo">
                    <option value="MOSTRADOR" ${editar.tipoProducto == 'MOSTRADOR' ? 'selected' : ''}>Mostrador</option>
                    <option value="ESPECIAL" ${editar.tipoProducto == 'ESPECIAL' ? 'selected' : ''}>Especial (por encargo)</option>
                </select></div>
            <div class="campo c12"><label>Descripción</label><input name="descripcion" maxlength="200" value="<c:out value='${editar.descripcion}'/>"></div>
            <div class="campo c4"><label>Precio sin IGV (S/) <span class="req">*</span></label><input name="precio" type="number" step="0.01" min="0" required value="${editar.precioUnitario}"></div>
            <div class="campo c4"><label>Preparación (min)</label><input name="tiempo" type="number" min="0" step="1" value="${editar.tiempoPreparacion}"></div>
            <div class="campo c4"><label class="check"><input type="checkbox" name="estado" ${empty editar or editar.estado ? 'checked' : ''}> Disponible para la venta</label></div>
            <div class="campo c12" style="flex-direction:row;gap:8px"><button type="submit" class="btn primario">Guardar producto</button>
                <c:if test="${not empty editar}"><a class="btn" href="${ctx}/admin?entidad=productos">Cancelar</a></c:if></div>
        </div>
    </form>

    <c:if test="${not empty editar}">
        <div class="tarjeta">
            <div class="titulo">Receta estándar <small>insumos para 1 unidad de <c:out value="${editar.nombre}" /></small></div>
            <div class="tabla-wrap"><table class="tabla">
                <thead><tr><th>Insumo</th><th class="num">Cantidad por unidad</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="r" items="${receta}">
                    <tr><td><c:out value="${r.insumo}" /></td>
                        <td class="num"><fmt:formatNumber value="${r.cantidad}" pattern="#,##0.###" /> ${r.unidad}</td>
                        <td><form method="post" action="${ctx}/admin" data-confirmar="¿Quitar ${r.insumo} de la receta?">
                            <input type="hidden" name="entidad" value="productos"><input type="hidden" name="accion" value="quitar">
                            <input type="hidden" name="id" value="${editar.idProducto}"><input type="hidden" name="idReceta" value="${r.idReceta}">
                            <button type="submit" class="btn chico peligro">Quitar</button></form></td></tr>
                </c:forEach>
                <c:if test="${empty receta}"><tr class="vacio"><td colspan="3">Sin receta: no se podrá calcular el consumo de insumos.</td></tr></c:if>
                </tbody>
            </table></div>
            <form method="post" action="${ctx}/admin" class="form" style="margin-top:14px">
                <input type="hidden" name="entidad" value="productos"><input type="hidden" name="accion" value="receta">
                <input type="hidden" name="id" value="${editar.idProducto}">
                <div class="campo c6"><label>Insumo</label>
                    <select name="idInsumo"><c:forEach var="i" items="${insumos}"><option value="${i.idInsumo}"><c:out value="${i.nombre}" /> (${i.unidadMedida})</option></c:forEach></select></div>
                <div class="campo c3" style="grid-column:span 3"><label>Cantidad</label><input name="cantidad" type="number" step="0.001" min="0.001" required></div>
                <div class="campo c3" style="grid-column:span 3"><button type="submit" class="btn ancho">Agregar / actualizar</button></div>
            </form>
        </div>
    </c:if>
</div>
<div class="herramientas">
    <div class="buscador"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>
        <input type="search" placeholder="Buscar producto..." data-filtrar="tablaProductos"></div>
</div>
<div class="tabla-wrap"><table class="tabla" id="tablaProductos">
    <thead><tr><th>Producto</th><th>Tipo</th><th class="num">Precio sin IGV</th><th class="num">Preparación</th><th>Estado</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="p" items="${lista}">
        <tr class="${p.estado ? '' : 'inactivo'}">
            <td><b><c:out value="${p.nombre}" /></b><br><small class="ayuda"><c:out value="${p.descripcion}" /></small></td>
            <td>${p.tipoProducto == 'ESPECIAL' ? 'Encargo' : 'Mostrador'}</td>
            <td class="num">S/ <fmt:formatNumber value="${p.precioUnitario}" pattern="#,##0.00" /></td>
            <td class="num">${p.tiempoPreparacion} min</td>
            <td><span class="etiqueta ${p.estado ? 'verde' : 'gris'}">${p.estado ? 'Disponible' : 'No disponible'}</span></td>
            <td><a class="btn chico" href="${ctx}/admin?entidad=productos&id=${p.idProducto}">Editar y receta</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table></div>
<%@ include file="/vista/comun/pie.jspf" %>
