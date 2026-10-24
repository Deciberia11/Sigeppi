package pe.deliciasdelvalle.sigeppi.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Orden de producción de la planta. */
public class OrdenProduccion {
    private int idOrden;
    private LocalDate fechaProduccion;
    private String estado = "EN_PROCESO";
    private Usuario jefe;
    private String observacion;
    private int nroPedidos;
    private List<DetalleOrden> detalles = new ArrayList<>();

    public int getIdOrden() { return idOrden; }
    public void setIdOrden(int idOrden) { this.idOrden = idOrden; }
    public LocalDate getFechaProduccion() { return fechaProduccion; }
    public void setFechaProduccion(LocalDate fechaProduccion) { this.fechaProduccion = fechaProduccion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Usuario getJefe() { return jefe; }
    public void setJefe(Usuario jefe) { this.jefe = jefe; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public int getNroPedidos() { return nroPedidos; }
    public void setNroPedidos(int nroPedidos) { this.nroPedidos = nroPedidos; }
    public List<DetalleOrden> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrden> detalles) { this.detalles = detalles; }
}
