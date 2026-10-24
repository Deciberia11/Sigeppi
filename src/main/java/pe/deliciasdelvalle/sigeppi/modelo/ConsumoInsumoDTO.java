package pe.deliciasdelvalle.sigeppi.modelo;

/** DTO: consumo histórico de un insumo en un periodo. */
public class ConsumoInsumoDTO {
    private String insumo;
    private String unidad;
    private double cantidad;
    private int nroOrdenes;
    private double porcentaje;

    public String getInsumo() { return insumo; }
    public void setInsumo(String insumo) { this.insumo = insumo; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public int getNroOrdenes() { return nroOrdenes; }
    public void setNroOrdenes(int nroOrdenes) { this.nroOrdenes = nroOrdenes; }
    public double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(double porcentaje) { this.porcentaje = porcentaje; }
}
