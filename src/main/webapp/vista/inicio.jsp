<%@ include file="/vista/comun/cabecera.jspf" %>
<%-- Panel de inicio por rol. Para el Administrador es el "panel de control gerencial" de la ES. --%>
<div class="cabecera-pag">
    <div>
        <h1>Hola, <c:out value="${u.nombres}" /></h1>
        <p>
            <c:choose>
                <c:when test="${rol == 'ADMINISTRADOR'}">Panel de control gerencial: así van las ventas, la producción y el almacén.</c:when>
                <c:when test="${rol == 'VENDEDOR'}">Resumen de <c:out value="${u.tienda.nombre}" />.</c:when>
                <c:when test="${rol == 'JEFE_PRODUCCION'}">Resumen de la planta de producción.</c:when>
                <c:otherwise>Resumen del almacén de insumos.</c:otherwise>
            </c:choose>
        </p>
    </div>
    <div class="acciones">
        <c:if test="${rol == 'VENDEDOR'}"><a class="btn primario" href="${ctx}/pedido">+ Registrar pedido</a></c:if>
        <c:if test="${rol == 'JEFE_PRODUCCION'}"><a class="btn primario" href="${ctx}/orden">+ Planificar orden</a></c:if>
        <c:if test="${rol == 'ENCARGADO_ALMACEN'}">
            <a class="btn" href="${ctx}/solicitud">Solicitudes de compra</a>
            <a class="btn primario" href="${ctx}/insumo">+ Registrar ingreso</a>
        </c:if>
        <c:if test="${rol == 'ADMINISTRADOR'}"><a class="btn primario" href="${ctx}/reporte">Ver reporte de ventas</a></c:if>
    </div>
</div>

<%-- ---------------------------------------------------------------- indicadores de ventas --%>
<c:if test="${not empty panel.ventasDia}">
    <div class="rejilla kpis">
        <div class="kpi verde"><span>Ventas de hoy</span>
            <b>S/ <fmt:formatNumber value="${panel.ventasDia.total}" pattern="#,##0.00" /></b>
            <small>${panel.ventasDia.cantidad} comprobante(s)</small></div>
        <div class="kpi"><span>Ventas del mes</span>
            <b>S/ <fmt:formatNumber value="${panel.ventasMes.total}" pattern="#,##0.00" /></b>
            <small>${panel.ventasMes.cantidad} comprobante(s)</small></div>
        <div class="kpi azul"><span>Ticket promedio del mes</span>
            <b>S/ <fmt:formatNumber value="${panel.ventasMes.promedio}" pattern="#,##0.00" /></b>
            <small>por comprobante</small></div>
        <c:choose>
            <c:when test="${rol == 'ADMINISTRADOR'}">
                <a class="kpi ${fn:length(panel.bajoMinimo) > 0 ? 'rojo' : 'verde'}" href="${ctx}/stock"><span>Insumos bajo el mínimo</span>
                    <b>${fn:length(panel.bajoMinimo)}</b><small>${panel.solicitudesPendientes} solicitud(es) de compra pendiente(s)</small></a>
            </c:when>
            <c:otherwise>
                <a class="kpi ambar" href="${ctx}/estadoPedido?estado=LISTO_ENTREGA"><span>Listos para entregar</span>
                    <b><c:forEach var="e" items="${panel.estados}"><c:if test="${e.estado == 'LISTO_ENTREGA'}">${e.cantidad}</c:if></c:forEach></b>
                    <small>pedidos esperando al cliente</small></a>
            </c:otherwise>
        </c:choose>
    </div>
</c:if>

<%-- ---------------------------------------------------------------- indicadores de almacén --%>
<c:if test="${rol == 'ENCARGADO_ALMACEN'}">
    <div class="rejilla kpis">
        <a class="kpi ${fn:length(panel.bajoMinimo) > 0 ? 'rojo' : 'verde'}" href="${ctx}/stock"><span>Insumos bajo el mínimo</span>
            <b>${fn:length(panel.bajoMinimo)}</b><small>revise y genere solicitudes</small></a>
        <a class="kpi ${fn:length(panel.porVencer) > 0 ? 'ambar' : 'verde'}" href="${ctx}/stock"><span>Lotes que vencen en 7 días</span>
            <b>${fn:length(panel.porVencer)}</b><small>úselos primero (FEFO)</small></a>
        <a class="kpi azul" href="${ctx}/solicitud?estado=PENDIENTE"><span>Solicitudes pendientes</span>
            <b>${panel.solicitudesPendientes}</b><small>esperando al proveedor</small></a>
        <a class="kpi" href="${ctx}/insumo?accion=salidas"><span>Kardex</span><b>&rarr;</b><small>ver salidas por producción</small></a>
    </div>
</c:if>

<%-- ---------------------------------------------------------------- pedidos por estado --%>
<c:if test="${not empty panel.estados}">
    <div class="tarjeta">
        <div class="titulo">Pedidos por estado <small>${rol == 'VENDEDOR' ? 'de mi tienda' : 'de todas las tiendas'}</small></div>
        <div class="estados">
            <c:forEach var="e" items="${panel.estados}">
                <c:choose>
                    <c:when test="${rol == 'ADMINISTRADOR'}">
                        <div><b>${e.cantidad}</b><span class="etiqueta ${e.estado}">${fn:replace(e.estado, '_', ' ')}</span></div>
                    </c:when>
                    <c:otherwise>
                        <a href="${ctx}/estadoPedido?estado=${e.estado}"><b>${e.cantidad}</b><span class="etiqueta ${e.estado}">${fn:replace(e.estado, '_', ' ')}</span></a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>
    </div>
</c:if>

<%-- ---------------------------------------------------------------- gráficos del administrador --%>
<c:if test="${rol == 'ADMINISTRADOR'}">
    <div class="rejilla dos" style="margin-bottom:20px">
        <div class="tarjeta">
            <div class="titulo">Ventas por tienda <small>mes actual</small></div>
            <div class="barras">
                <c:forEach var="t" items="${panel.porTienda}">
                    <div class="barra-fila"><span><c:out value="${t.tienda}" /></span>
                        <div class="pista"><div class="relleno" style="width:${t.pct}%"></div></div>
                        <span class="valor">S/ <fmt:formatNumber value="${t.total}" pattern="#,##0.00" /></span></div>
                </c:forEach>
            </div>
        </div>
        <div class="tarjeta">
            <div class="titulo">Ventas de los últimos 7 días</div>
            <div class="columnas">
                <c:forEach var="d" items="${panel.ultimosDias}">
                    <div class="col" title="S/ ${d.total}">
                        <b><fmt:formatNumber value="${d.total}" pattern="#,##0" /></b>
                        <div class="c" style="height:${d.pct}%"></div>
                        <small>${d.dia}</small>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
    <div class="tarjeta">
        <div class="titulo">Productos más vendidos <small>mes actual</small></div>
        <div class="tabla-wrap"><table class="tabla">
            <thead><tr><th>Producto</th><th class="num">Unidades</th><th class="num">Importe (sin IGV)</th></tr></thead>
            <tbody>
            <c:forEach var="p" items="${panel.topProductos}">
                <tr><td><c:out value="${p.producto}" /></td><td class="num">${p.unidades}</td>
                    <td class="num">S/ <fmt:formatNumber value="${p.total}" pattern="#,##0.00" /></td></tr>
            </c:forEach>
            <c:if test="${empty panel.topProductos}"><tr class="vacio"><td colspan="3">Todavía no hay ventas este mes.</td></tr></c:if>
            </tbody>
        </table></div>
    </div>
</c:if>

<div class="rejilla dos">
    <%-- ---------------------------------------------------------------- encargos próximos --%>
    <c:if test="${rol == 'VENDEDOR' or rol == 'JEFE_PRODUCCION' or rol == 'ADMINISTRADOR'}">
        <div class="tarjeta">
            <div class="titulo">Encargos especiales por entregar <small>próximos días</small></div>
            <div class="tabla-wrap"><table class="tabla">
                <thead><tr><th>N.°</th><th>Cliente</th><th>Entrega</th><th>Estado</th></tr></thead>
                <tbody>
                <c:forEach var="p" items="${panel.especiales}">
                    <tr><td>${p.id}</td><td><c:out value="${p.cliente}" /><br><small class="ayuda"><c:out value="${p.tienda}" /></small></td>
                        <td>${p.entrega}</td><td><span class="etiqueta ${p.estado}">${fn:replace(p.estado, '_', ' ')}</span></td></tr>
                </c:forEach>
                <c:if test="${empty panel.especiales}"><tr class="vacio"><td colspan="4">No hay encargos próximos.</td></tr></c:if>
                </tbody>
            </table></div>
        </div>
    </c:if>

    <%-- ---------------------------------------------------------------- stock mínimo (alerta al encargado) --%>
    <c:if test="${rol != 'VENDEDOR'}">
        <div class="tarjeta">
            <div class="titulo">Insumos por debajo del stock mínimo
                <c:if test="${rol == 'ENCARGADO_ALMACEN' or rol == 'ADMINISTRADOR'}"><a class="btn chico" href="${ctx}/solicitud">Solicitar compra</a></c:if>
            </div>
            <div class="tabla-wrap"><table class="tabla">
                <thead><tr><th>Insumo</th><th class="num">Stock</th><th class="num">Mínimo</th></tr></thead>
                <tbody>
                <c:forEach var="i" items="${panel.bajoMinimo}">
                    <tr><td><c:out value="${i.insumo}" /></td>
                        <td class="num"><span class="etiqueta rojo"><fmt:formatNumber value="${i.stock}" pattern="#,##0.###" /> ${i.unidad}</span></td>
                        <td class="num"><fmt:formatNumber value="${i.minimo}" pattern="#,##0.###" /> ${i.unidad}</td></tr>
                </c:forEach>
                <c:if test="${empty panel.bajoMinimo}"><tr class="vacio"><td colspan="3">Todos los insumos están sobre el mínimo.</td></tr></c:if>
                </tbody>
            </table></div>
        </div>
    </c:if>

    <%-- ---------------------------------------------------------------- lotes por vencer --%>
    <c:if test="${rol == 'ENCARGADO_ALMACEN' or rol == 'ADMINISTRADOR' or rol == 'JEFE_PRODUCCION'}">
        <div class="tarjeta">
            <div class="titulo">Lotes que vencen en los próximos 7 días</div>
            <div class="tabla-wrap"><table class="tabla">
                <thead><tr><th>Lote</th><th>Insumo</th><th class="num">Disponible</th><th>Vence</th></tr></thead>
                <tbody>
                <c:forEach var="l" items="${panel.porVencer}">
                    <tr><td><c:out value="${l.lote}" /></td><td><c:out value="${l.insumo}" /></td>
                        <td class="num"><fmt:formatNumber value="${l.disponible}" pattern="#,##0.###" /> ${l.unidad}</td>
                        <td><span class="etiqueta ${l.dias < 0 ? 'rojo' : 'ambar'}">${l.vence}${l.dias < 0 ? ' (vencido)' : ''}</span></td></tr>
                </c:forEach>
                <c:if test="${empty panel.porVencer}"><tr class="vacio"><td colspan="4">No hay lotes por vencer.</td></tr></c:if>
                </tbody>
            </table></div>
        </div>
    </c:if>
</div>
<%@ include file="/vista/comun/pie.jspf" %>
