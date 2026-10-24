package pe.deliciasdelvalle.sigeppi.servicio;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import pe.deliciasdelvalle.sigeppi.modelo.Cliente;
import pe.deliciasdelvalle.sigeppi.modelo.DetallePedido;
import pe.deliciasdelvalle.sigeppi.modelo.EstadoPedido;
import pe.deliciasdelvalle.sigeppi.modelo.Insumo;
import pe.deliciasdelvalle.sigeppi.modelo.LoteInsumo;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.Pedido;
import pe.deliciasdelvalle.sigeppi.modelo.Producto;
import pe.deliciasdelvalle.sigeppi.modelo.Proveedor;
import pe.deliciasdelvalle.sigeppi.modelo.Rol;
import pe.deliciasdelvalle.sigeppi.modelo.Tienda;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;

/** Pruebas de las reglas de negocio que no necesitan base de datos. */
class ReglasNegocioTest {

    private final PedidoService pedidoService = new PedidoService();
    private final InventarioService inventarioService = new InventarioService();
    private final ReporteService reporteService = new ReporteService();

    private Pedido pedidoBase() {
        Pedido p = new Pedido();
        Cliente c = new Cliente();
        c.setIdCliente(1);                      // cliente ya registrado
        p.setCliente(c);
        Tienda t = new Tienda();
        t.setIdTienda(1);
        p.setTienda(t);
        return p;
    }

    private DetallePedido detalle(int cantidad, double precio) {
        DetallePedido d = new DetallePedido();
        d.setProducto(new Producto());
        d.setCantidad(cantidad);
        d.setPrecioUnitario(precio);
        return d;
    }

    private Usuario usuario(String rol, int idTienda) {
        Usuario u = new Usuario();
        Rol r = new Rol();
        r.setNombre(rol);
        u.setRol(r);
        Tienda t = new Tienda();
        t.setIdTienda(idTienda);
        u.setTienda(t);
        return u;
    }

    @Test
    void rn003_calculaSubtotalIgvYTotal() {
        Pedido p = pedidoBase();
        p.agregarDetalle(detalle(2, 10.0));   // 20.00
        p.agregarDetalle(detalle(1, 5.5));    //  5.50
        pedidoService.calcularTotales(p);
        assertEquals(25.50, p.getSubtotal(), 0.001);
        assertEquals(4.59, p.getIgv(), 0.001);
        assertEquals(30.09, p.getTotal(), 0.001);
    }

    @Test
    void rn002_pedidoSinProductosNoSeConfirma() {
        assertThrows(NegocioException.class, () -> pedidoService.validar(pedidoBase()));
    }

    @Test
    void rn001_clienteNuevoSinNombreNoSeAcepta() {
        Pedido p = pedidoBase();
        p.setCliente(new Cliente());
        p.agregarDetalle(detalle(1, 10.0));
        assertThrows(NegocioException.class, () -> pedidoService.validar(p));
    }

    @Test
    void rn005_cantidadCeroONegativaNoSeAcepta() {
        Pedido p = pedidoBase();
        p.agregarDetalle(detalle(0, 10.0));
        assertThrows(NegocioException.class, () -> pedidoService.validar(p));
        p.getDetalles().clear();
        p.agregarDetalle(detalle(3, 10.0));
        assertDoesNotThrow(() -> pedidoService.validar(p));
    }

    @Test
    void rn007_estadosAvanzanSinSaltos() {
        assertEquals(EstadoPedido.EN_PRODUCCION, EstadoPedido.REGISTRADO.siguiente());
        assertEquals(EstadoPedido.LISTO_ENTREGA, EstadoPedido.EN_PRODUCCION.siguiente());
        assertEquals(EstadoPedido.ENTREGADO, EstadoPedido.LISTO_ENTREGA.siguiente());
        assertNull(EstadoPedido.ENTREGADO.siguiente());
    }

    @Test
    void rn008_registradoAEnProduccionSoloPorOrden() {
        Pedido p = pedidoBase();
        Usuario jefe = usuario(Rol.JEFE_PRODUCCION, 0);
        assertThrows(NegocioException.class,
                () -> pedidoService.validarPermiso(jefe, p, EstadoPedido.EN_PRODUCCION));
    }

    @Test
    void cus03_permisosPorRol() {
        Pedido p = pedidoBase();
        Usuario vendedorMismaTienda = usuario(Rol.VENDEDOR, 1);
        Usuario vendedorOtraTienda = usuario(Rol.VENDEDOR, 2);
        Usuario jefe = usuario(Rol.JEFE_PRODUCCION, 0);
        assertDoesNotThrow(() -> pedidoService.validarPermiso(jefe, p, EstadoPedido.LISTO_ENTREGA));
        assertThrows(NegocioException.class, () -> pedidoService.validarPermiso(vendedorMismaTienda, p, EstadoPedido.LISTO_ENTREGA));
        assertDoesNotThrow(() -> pedidoService.validarPermiso(vendedorMismaTienda, p, EstadoPedido.ENTREGADO));
        assertThrows(NegocioException.class, () -> pedidoService.validarPermiso(vendedorOtraTienda, p, EstadoPedido.ENTREGADO));
        assertThrows(NegocioException.class, () -> pedidoService.validarPermiso(jefe, p, null));
    }

    @Test
    void rn006_vencimientoDebeSerPosteriorAlIngreso() {
        LoteInsumo l = new LoteInsumo();
        l.setInsumo(new Insumo());
        l.setProveedor(new Proveedor());
        l.setCantidadInicial(10);
        l.setCostoUnitario(2.5);
        l.setFechaIngreso(LocalDate.of(2026, 10, 24));
        l.setFechaVencimiento(LocalDate.of(2026, 10, 24));   // mismo día: no válido
        assertThrows(NegocioException.class, () -> inventarioService.validar(l));
        l.setFechaVencimiento(LocalDate.of(2026, 11, 10));
        assertDoesNotThrow(() -> inventarioService.validar(l));
        l.setCantidadInicial(-1);                             // RN005
        assertThrows(NegocioException.class, () -> inventarioService.validar(l));
    }

    @Test
    void cus07_rangoDeFechasInvalido() {
        assertThrows(NegocioException.class,
                () -> reporteService.validarRango(LocalDate.of(2026, 10, 30), LocalDate.of(2026, 10, 1)));
        assertDoesNotThrow(() -> reporteService.validarRango(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 30)));
    }
}
