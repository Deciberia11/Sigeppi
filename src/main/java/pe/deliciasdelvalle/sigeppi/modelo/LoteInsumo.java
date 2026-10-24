package pe.deliciasdelvalle.sigeppi.modelo;

import java.time.LocalDate;

/** Lote de un insumo con su fecha de vencimiento. */
public class LoteInsumo {
    private int idLote;
    private String codigo;
    private Insumo insumo;
    private Proveedor proveedor;
    private LocalDate fechaIngreso;
    private LocalDate fechaVencimiento;
    private double cantidadInicial;
    private double cantidadDisponible;
    private double costoUnitario;

    public int getIdLote() { return idLote; }
    public void setIdLote(int idLote) { this.idLote = idLote; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public Insumo getInsumo() { return insumo; }
    public void setInsumo(Insumo insumo) { this.insumo = insumo; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public double getCantidadInicial() { return cantidadInicial; }
    public void setCantidadInicial(double cantidadInicial) { this.cantidadInicial = cantidadInicial; }
    public double getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(double cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
    public double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(double costoUnitario) { this.costoUnitario = costoUnitario; }

    public long getDiasParaVencer() {
        return fechaVencimiento == null ? 0 : java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), fechaVencimiento);
    }
}
