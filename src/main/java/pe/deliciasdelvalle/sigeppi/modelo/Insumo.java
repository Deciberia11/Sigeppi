package pe.deliciasdelvalle.sigeppi.modelo;

/** Insumo del almacén. */
public class Insumo {
    private int idInsumo;
    private String nombre;
    private String unidadMedida;
    private double stockActual;
    private double stockMinimo;
    private boolean perecible;

    public int getIdInsumo() { return idInsumo; }
    public void setIdInsumo(int idInsumo) { this.idInsumo = idInsumo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public double getStockActual() { return stockActual; }
    public void setStockActual(double stockActual) { this.stockActual = stockActual; }
    public double getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(double stockMinimo) { this.stockMinimo = stockMinimo; }
    public boolean isPerecible() { return perecible; }
    public void setPerecible(boolean perecible) { this.perecible = perecible; }

    public boolean estaBajoMinimo() {
        return stockActual < stockMinimo;
    }
}
