<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- Insumos (RNF-10). El stock no se edita aquí: solo cambia con ingresos y salidas (kardex). --%>
<div class="cabecera-pag">
    <div><h1>Insumos</h1><p>Catálogo de insumos y su stock mínimo. El stock solo cambia con los ingresos de lotes y las salidas por producción.</p></div>
    <div class="acciones"><a class="btn" href="${ctx}/admin?entidad=insumos">+ Nuevo insumo</a></div>
</div>
<form method="post" action="${ctx}/admin" class="tarjeta" id="formInsumo">
    <input type="hidden" name="entidad" value="insumos"><input type="hidden" name="accion" value="guardar"><input type="hidden" name="id" value="0">
    <h3 data-titulo="Editar insumo">Nuevo insumo</h3>
    <div class="form">
        <div class="campo c4"><label>Nombre <span class="req">*</span></label><input name="nombre" maxlength="60" required></div>
        <div class="campo c2"><label>Unidad <span class="req">*</span></label>
            <select name="unidad"><option value="kg">kg</option><option value="l">l</option><option value="und">und</option><option value="g">g</option></select></div>
        <div class="campo c2"><label>Stock mínimo <span class="req">*</span></label><input name="minimo" type="number" step="0.001" min="0" required></div>
        <div class="campo c2"><label class="check"><input type="checkbox" name="perecible"> Perecible</label></div>
        <div class="campo c2"><button type="submit" class="btn primario ancho">Guardar</button></div>
    </div>
</form>
<div class="herramientas"><div class="buscador"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>
    <input type="search" placeholder="Buscar insumo..." data-filtrar="tablaInsumos"></div></div>
<div class="tabla-wrap"><table class="tabla" id="tablaInsumos">
    <thead><tr><th>Insumo</th><th>Unidad</th><th>Tipo</th><th class="num">Stock actual</th><th class="num">Stock mínimo</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="i" items="${lista}">
        <tr><td><b><c:out value="${i.nombre}" /></b></td><td>${i.unidadMedida}</td><td>${i.perecible ? 'Perecible' : 'Seco'}</td>
            <td class="num"><span class="etiqueta ${i.stockActual < i.stockMinimo ? 'rojo' : 'verde'}"><fmt:formatNumber value="${i.stockActual}" pattern="#,##0.###" /></span></td>
            <td class="num"><fmt:formatNumber value="${i.stockMinimo}" pattern="#,##0.###" /></td>
            <td><button type="button" class="btn chico" data-editar="formInsumo" data-f-id="${i.idInsumo}" data-f-nombre="<c:out value='${i.nombre}'/>"
                        data-f-unidad="${i.unidadMedida}" data-f-minimo="${i.stockMinimo}" data-f-perecible="${i.perecible}">Editar</button></td></tr>
    </c:forEach>
    </tbody>
</table></div>
<%@ include file="/vista/comun/pie.jspf" %>
