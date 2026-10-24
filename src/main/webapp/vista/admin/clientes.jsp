<%@ include file="/vista/comun/cabecera.jspf" %>
<div class="cabecera-pag">
    <div><h1>Clientes</h1><p>Los clientes se registran desde "Registrar pedido". Aquí se corrigen sus datos. Se tratan según la Ley N.° 29733.</p></div>
</div>
<form method="post" action="${ctx}/admin" class="tarjeta oculto" id="formCliente">
    <input type="hidden" name="entidad" value="clientes"><input type="hidden" name="accion" value="guardar"><input type="hidden" name="id" value="0">
    <h3>Editar cliente</h3>
    <div class="form">
        <div class="campo c6"><label>Nombre <span class="req">*</span></label><input name="nombres" maxlength="100" required></div>
        <div class="campo c2"><label>Teléfono</label><input name="telefono" maxlength="15"></div>
        <div class="campo c2"><label>Tipo</label><select name="tipo"><option value="FRECUENTE">Frecuente</option><option value="GENERICO">Genérico</option></select></div>
        <div class="campo c2"><button type="submit" class="btn primario ancho">Guardar</button></div>
    </div>
</form>
<div class="herramientas"><div class="buscador"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>
    <input type="search" placeholder="Buscar cliente..." data-filtrar="tablaClientes"></div></div>
<div class="tabla-wrap"><table class="tabla" id="tablaClientes">
    <thead><tr><th>Cliente</th><th>Teléfono</th><th>Tipo</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="c" items="${lista}">
        <tr><td><b><c:out value="${c.nombres}" /></b></td><td><c:out value="${c.telefono}" /></td>
            <td><span class="etiqueta ${c.tipoCliente == 'GENERICO' ? 'gris' : 'azul'}">${c.tipoCliente}</span></td>
            <td><button type="button" class="btn chico btn-cliente" data-editar="formCliente" data-f-id="${c.idCliente}" data-f-nombres="<c:out value='${c.nombres}'/>"
                        data-f-telefono="<c:out value='${c.telefono}'/>" data-f-tipo="${c.tipoCliente}">Editar</button></td></tr>
    </c:forEach>
    </tbody>
</table></div>
<script>
    document.querySelectorAll('.btn-cliente').forEach(function (b) {
        b.addEventListener('click', function () { document.getElementById('formCliente').classList.remove('oculto'); }, true);
    });
</script>
<%@ include file="/vista/comun/pie.jspf" %>
