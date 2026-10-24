<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- CUS06 - Registrar Ingreso de Insumos (RN005, RN006) --%>
<div class="cabecera-pag">
    <div><h1>Registrar ingreso de insumos</h1><p>Cada ingreso es un lote con su propia fecha de vencimiento (RN006).</p></div>
    <div class="acciones"><a class="btn" href="${ctx}/stock">Stock y lotes</a><a class="btn" href="${ctx}/solicitud">Solicitudes de compra</a></div>
</div>
<form method="post" action="${ctx}/insumo" class="tarjeta" id="formIngreso">
    <h3>Datos del lote</h3>
    <div class="form">
        <div class="campo c4">
            <label for="idInsumo">Insumo <span class="req">*</span></label>
            <select name="idInsumo" id="idInsumo" required>
                <c:forEach var="i" items="${insumos}">
                    <option value="${i.idInsumo}" ${param.idInsumo == i.idInsumo ? 'selected' : ''}><c:out value="${i.nombre}" /> (${i.unidadMedida})</option>
                </c:forEach>
            </select>
        </div>
        <div class="campo c4">
            <label for="idProveedor">Proveedor <span class="req">*</span></label>
            <select name="idProveedor" id="idProveedor" required>
                <c:forEach var="pv" items="${proveedores}">
                    <option value="${pv.idProveedor}"><c:out value="${pv.razonSocial}" /></option>
                </c:forEach>
            </select>
        </div>
        <div class="campo c4">
            <label for="codigoLote">Código de lote <span class="req">*</span></label>
            <input type="text" name="codigoLote" id="codigoLote" maxlength="20" required placeholder="Ej. HAR-2610-01">
        </div>
        <div class="campo">
            <label for="cantidad">Cantidad <span class="req">*</span></label>
            <input type="number" name="cantidad" id="cantidad" step="0.001" min="0.001" required>
        </div>
        <div class="campo">
            <label for="costoUnitario">Costo unitario (S/) <span class="req">*</span></label>
            <input type="number" name="costoUnitario" id="costoUnitario" step="0.01" min="0" required>
        </div>
        <div class="campo">
            <label for="fechaIngreso">Fecha de ingreso <span class="req">*</span></label>
            <input type="date" name="fechaIngreso" id="fechaIngreso" required>
        </div>
        <div class="campo">
            <label for="fechaVencimiento">Fecha de vencimiento <span class="req">*</span></label>
            <input type="date" name="fechaVencimiento" id="fechaVencimiento" required>
        </div>
        <div class="campo c12"><button type="submit" class="btn primario">Guardar ingreso</button>
            <span class="ayuda">Al guardar se suma el stock y se marca como atendida la solicitud de compra pendiente del insumo.</span></div>
    </div>
</form>

<div class="tarjeta">
    <div class="titulo">Stock actual de insumos</div>
    <div class="tabla-wrap"><table class="tabla">
        <thead><tr><th>Insumo</th><th class="num">Stock actual</th><th class="num">Stock mínimo</th><th>Estado</th></tr></thead>
        <tbody>
        <c:forEach var="i" items="${insumos}">
            <tr>
                <td><c:out value="${i.nombre}" /></td>
                <td class="num"><fmt:formatNumber value="${i.stockActual}" pattern="#,##0.###" /> ${i.unidadMedida}</td>
                <td class="num"><fmt:formatNumber value="${i.stockMinimo}" pattern="#,##0.###" /> ${i.unidadMedida}</td>
                <td><span class="etiqueta ${i.stockActual < i.stockMinimo ? 'rojo' : 'verde'}">${i.stockActual < i.stockMinimo ? 'Bajo el mínimo' : 'OK'}</span></td>
            </tr>
        </c:forEach>
        </tbody>
    </table></div>
</div>
<script>
    var fi = document.getElementById('fechaIngreso');
    if (!fi.value) { fi.value = new Date().toLocaleDateString('en-CA'); }
    // RN006: aviso rápido en pantalla (el servidor y la BD también lo validan)
    document.getElementById('formIngreso').addEventListener('submit', function (e) {
        var ing = document.getElementById('fechaIngreso').value;
        var ven = document.getElementById('fechaVencimiento').value;
        if (ing && ven && ven <= ing) {
            e.preventDefault();
            alert('La fecha de vencimiento debe ser posterior a la fecha de ingreso (RN006).');
        }
    });
</script>
<%@ include file="/vista/comun/pie.jspf" %>
