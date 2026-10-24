<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- Administración de usuarios (RNF-10, RNF-16: un usuario y clave por trabajador, acceso según su rol) --%>
<div class="cabecera-pag">
    <div><h1>Usuarios</h1><p>Cuentas de los trabajadores. Cada rol solo ve los módulos que le corresponden.</p></div>
    <div class="acciones"><a class="btn" href="${ctx}/admin?entidad=usuarios">+ Nuevo usuario</a></div>
</div>
<form method="post" action="${ctx}/admin" class="tarjeta" id="formUsuario">
    <input type="hidden" name="entidad" value="usuarios">
    <input type="hidden" name="accion" value="guardar">
    <input type="hidden" name="id" value="0">
    <h3 data-titulo="Editar usuario">Nuevo usuario</h3>
    <div class="form">
        <div class="campo"><label>Usuario <span class="req">*</span></label><input name="nombreUsuario" maxlength="30" required pattern="[A-Za-z0-9._]{4,30}"></div>
        <div class="campo"><label>Nombres <span class="req">*</span></label><input name="nombres" maxlength="60" required></div>
        <div class="campo"><label>Apellidos <span class="req">*</span></label><input name="apellidos" maxlength="60" required></div>
        <div class="campo"><label>Rol <span class="req">*</span></label>
            <select name="idRol" id="selRol">
                <option value="1">Vendedor de Tienda</option>
                <option value="2">Jefe de Producción</option>
                <option value="3">Encargado de Almacén</option>
                <option value="4">Administrador</option>
            </select></div>
        <div class="campo" id="campoTienda"><label>Tienda (vendedores)</label>
            <select name="idTienda">
                <option value="0">— Ninguna —</option>
                <c:forEach var="t" items="${tiendas}"><option value="${t.idTienda}"><c:out value="${t.nombre}" /></option></c:forEach>
            </select></div>
        <div class="campo solo-nuevo"><label>Contraseña inicial <span class="req">*</span></label>
            <input name="clave" type="text" minlength="8" placeholder="mín. 8, letras y números"></div>
        <div class="campo c6" style="flex-direction:row;gap:8px">
            <button type="submit" class="btn primario">Guardar</button>
            <a class="btn" href="${ctx}/admin?entidad=usuarios">Cancelar</a></div>
    </div>
</form>
<div class="herramientas">
    <div class="buscador"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>
        <input type="search" placeholder="Buscar usuario..." data-filtrar="tablaUsuarios"></div>
</div>
<div class="tabla-wrap"><table class="tabla" id="tablaUsuarios">
    <thead><tr><th>Usuario</th><th>Nombre</th><th>Rol</th><th>Tienda</th><th>Último acceso</th><th>Estado</th><th>Acciones</th></tr></thead>
    <tbody>
    <c:forEach var="x" items="${lista}">
        <tr class="${x.estado ? '' : 'inactivo'}">
            <td><b><c:out value="${x.nombreUsuario}" /></b></td>
            <td><c:out value="${x.nombreCompleto}" /></td>
            <td>${fn:replace(x.rol.nombre, '_', ' ')}</td>
            <td><c:out value="${x.tienda.nombre}" /></td>
            <td>${x.ultimoAccesoTexto}</td>
            <td><span class="etiqueta ${x.estado ? 'verde' : 'gris'}">${x.estado ? 'Activo' : 'Inactivo'}</span></td>
            <td class="en-linea">
                <button type="button" class="btn chico" data-editar="formUsuario" data-f-id="${x.idUsuario}"
                        data-f-nombreUsuario="<c:out value='${x.nombreUsuario}'/>" data-f-nombres="<c:out value='${x.nombres}'/>"
                        data-f-apellidos="<c:out value='${x.apellidos}'/>" data-f-idRol="${x.rol.idRol}"
                        data-f-idTienda="${empty x.tienda ? 0 : x.tienda.idTienda}">Editar</button>
                <form method="post" action="${ctx}/admin" data-confirmar="¿${x.estado ? 'Desactivar' : 'Activar'} al usuario ${x.nombreUsuario}?">
                    <input type="hidden" name="entidad" value="usuarios"><input type="hidden" name="accion" value="estado">
                    <input type="hidden" name="id" value="${x.idUsuario}"><input type="hidden" name="activo" value="${not x.estado}">
                    <button type="submit" class="btn chico ${x.estado ? 'peligro' : 'exito'}">${x.estado ? 'Desactivar' : 'Activar'}</button>
                </form>
                <form method="post" action="${ctx}/admin" class="form-clave">
                    <input type="hidden" name="entidad" value="usuarios"><input type="hidden" name="accion" value="clave">
                    <input type="hidden" name="id" value="${x.idUsuario}"><input type="hidden" name="clave" value="">
                    <button type="submit" class="btn chico">Restablecer clave</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table></div>
<script>
    // pedir la nueva clave al restablecer
    document.querySelectorAll('.form-clave').forEach(function (f) {
        f.addEventListener('submit', function (e) {
            var c = prompt('Nueva contraseña para este usuario (mínimo 8 caracteres, con letras y números):');
            if (!c) { e.preventDefault(); e.stopImmediatePropagation(); return; }
            f.elements['clave'].value = c;
        });
    });
    // la tienda solo aplica a vendedores
    var rol = document.getElementById('selRol');
    function verTienda() { document.getElementById('campoTienda').style.opacity = rol.value === '1' ? '1' : '.45'; }
    rol.addEventListener('change', verTienda); verTienda();
</script>
<%@ include file="/vista/comun/pie.jspf" %>
