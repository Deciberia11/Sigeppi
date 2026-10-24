<%@ include file="/vista/comun/cabecera.jspf" %>
<div class="cabecera-pag">
    <div><h1>Proveedores</h1><p>Contactos de los proveedores para las solicitudes de compra.</p></div>
    <div class="acciones"><a class="btn" href="${ctx}/admin?entidad=proveedores">+ Nuevo proveedor</a></div>
</div>
<form method="post" action="${ctx}/admin" class="tarjeta" id="formProveedor">
    <input type="hidden" name="entidad" value="proveedores"><input type="hidden" name="accion" value="guardar"><input type="hidden" name="id" value="0">
    <h3 data-titulo="Editar proveedor">Nuevo proveedor</h3>
    <div class="form">
        <div class="campo c4"><label>Razón social <span class="req">*</span></label><input name="razonSocial" maxlength="100" required></div>
        <div class="campo c2"><label>RUC <span class="req">*</span></label><input name="ruc" maxlength="11" pattern="\d{11}" required></div>
        <div class="campo c2"><label>Teléfono</label><input name="telefono" maxlength="15"></div>
        <div class="campo c2"><label>Correo</label><input name="correo" type="email" maxlength="80"></div>
        <div class="campo c2"><label>Tipo</label><select name="tipo"><option value="SECOS">Secos</option><option value="PERECIBLES">Perecibles</option></select></div>
        <div class="campo c12"><button type="submit" class="btn primario">Guardar</button></div>
    </div>
</form>
<div class="tabla-wrap"><table class="tabla">
    <thead><tr><th>Razón social</th><th>RUC</th><th>Teléfono</th><th>Correo</th><th>Tipo</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="p" items="${lista}">
        <tr><td><b><c:out value="${p.razonSocial}" /></b></td><td>${p.ruc}</td><td><c:out value="${p.telefono}" /></td>
            <td><c:if test="${not empty p.correo}"><a href="mailto:${p.correo}"><c:out value="${p.correo}" /></a></c:if></td>
            <td>${p.tipoProveedor}</td>
            <td><button type="button" class="btn chico" data-editar="formProveedor" data-f-id="${p.idProveedor}" data-f-razonSocial="<c:out value='${p.razonSocial}'/>"
                        data-f-ruc="${p.ruc}" data-f-telefono="<c:out value='${p.telefono}'/>" data-f-correo="<c:out value='${p.correo}'/>" data-f-tipo="${p.tipoProveedor}">Editar</button></td></tr>
    </c:forEach>
    </tbody>
</table></div>
<%@ include file="/vista/comun/pie.jspf" %>
