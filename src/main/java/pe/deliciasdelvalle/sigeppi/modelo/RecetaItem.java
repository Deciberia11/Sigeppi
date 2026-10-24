package pe.deliciasdelvalle.sigeppi.modelo;

/** Línea de la receta estándar de un producto (RN004). */
public class RecetaItem {
    private int idReceta;
    private int idInsumo;
    private String insumo;
    private String unidad;
    private double cantidad;

    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }
    public int getIdInsumo() { return idInsumo; }
    public void setIdInsumo(int idInsumo) { this.idInsumo = idInsumo; }
    public String getInsumo() { return insumo; }
    public void setInsumo(String insumo) { this.insumo = insumo; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
}
