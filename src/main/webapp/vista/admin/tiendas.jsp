<%@ include file="/vista/comun/cabecera.jspf" %>
<div class="cabecera-pag">
    <div><h1>Tiendas</h1><p>Locales de venta. Se pueden agregar nuevas tiendas sin cambiar el sistema (RNF-10).</p></div>
    <div class="acciones"><a class="btn" href="${ctx}/admin?entidad=tiendas">+ Nueva tienda</a></div>
</div>
<form method="post" action="${ctx}/admin" class="tarjeta" id="formTienda">
    <input type="hidden" name="entidad" value="tiendas"><input type="hidden" name="accion" value="guardar"><input type="hidden" name="id" value="0">
    <h3 data-titulo="Editar tienda">Nueva tienda</h3>
    <div class="form">
        <div class="campo"><label>Nombre <span class="req">*</span></label><input name="nombre" maxlength="60" required></div>
        <div class="campo c4"><label>Dirección</label><input name="direccion" maxlength="150"></div>
        <div class="campo c2"><label>Distrito</label><input name="distrito" maxlength="40"></div>
        <div class="campo c2"><label class="check"><input type="checkbox" name="estado" checked> Activa</label></div>
        <div class="campo c1" style="grid-column:span 1"><button type="submit" class="btn primario">Guardar</button></div>
    </div>
</form>
<div class="tabla-wrap"><table class="tabla">
    <thead><tr><th>Tienda</th><th>Dirección</th><th>Distrito</th><th>Estado</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="t" items="${lista}">
        <tr class="${t.estado ? '' : 'inactivo'}"><td><b><c:out value="${t.nombre}" /></b></td><td><c:out value="${t.direccion}" /></td><td><c:out value="${t.distrito}" /></td>
            <td><span class="etiqueta ${t.estado ? 'verde' : 'gris'}">${t.estado ? 'Activa' : 'Inactiva'}</span></td>
            <td><button type="button" class="btn chico" data-editar="formTienda" data-f-id="${t.idTienda}" data-f-nombre="<c:out value='${t.nombre}'/>"
                        data-f-direccion="<c:out value='${t.direccion}'/>" data-f-distrito="<c:out value='${t.distrito}'/>" data-f-estado="${t.estado}">Editar</button></td></tr>
    </c:forEach>
    </tbody>
</table></div>
<%@ include file="/vista/comun/pie.jspf" %>
