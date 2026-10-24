<%@ include file="/vista/comun/cabecera.jspf" %>
<div class="cabecera-pag">
    <div><h1>Cambiar contraseña</h1><p>Por seguridad, use una contraseña que no comparta con nadie (RNF-01).</p></div>
</div>
<div class="rejilla dos">
    <form method="post" action="${ctx}/cuenta" class="tarjeta" id="formClave">
        <h3>Nueva contraseña</h3>
        <div class="form">
            <div class="campo c12">
                <label for="actual">Contraseña actual <span class="req">*</span></label>
                <input type="password" id="actual" name="actual" required autocomplete="current-password">
            </div>
            <div class="campo c6">
                <label for="nueva">Nueva contraseña <span class="req">*</span></label>
                <input type="password" id="nueva" name="nueva" required minlength="8" autocomplete="new-password">
                <span class="ayuda">Mínimo 8 caracteres, con letras y números.</span>
            </div>
            <div class="campo c6">
                <label for="confirmar">Repita la nueva contraseña <span class="req">*</span></label>
                <input type="password" id="confirmar" name="confirmar" required minlength="8" autocomplete="new-password">
                <span class="ayuda">&nbsp;</span>
            </div>
            <div class="campo c12"><button type="submit" class="btn primario">Guardar contraseña</button></div>
        </div>
    </form>
    <div class="tarjeta">
        <h3>Mis datos</h3>
        <table class="tabla">
            <tr><td>Usuario</td><td><b><c:out value="${u.nombreUsuario}" /></b></td></tr>
            <tr><td>Nombre</td><td><c:out value="${u.nombreCompleto}" /></td></tr>
            <tr><td>Rol</td><td><c:out value="${rol}" /></td></tr>
            <c:if test="${not empty u.tienda}"><tr><td>Tienda</td><td><c:out value="${u.tienda.nombre}" /></td></tr></c:if>
            <tr><td>Último acceso anterior</td><td><c:out value="${u.ultimoAccesoTexto}" /></td></tr>
        </table>
        <p class="ayuda">La sesión se cierra sola a los 15 minutos sin actividad (RNF-02).</p>
    </div>
</div>
<script>
    document.getElementById('formClave').addEventListener('submit', function (e) {
        var n = document.getElementById('nueva').value, c = document.getElementById('confirmar').value;
        if (n !== c) { e.preventDefault(); alert('La confirmación no coincide con la nueva contraseña.'); }
        else if (n.length < 8 || !/[A-Za-z]/.test(n) || !/\d/.test(n)) {
            e.preventDefault(); alert('La nueva contraseña debe tener al menos 8 caracteres, con letras y números.');
        }
    });
</script>
<%@ include file="/vista/comun/pie.jspf" %>
