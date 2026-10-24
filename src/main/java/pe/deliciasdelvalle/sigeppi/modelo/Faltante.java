package pe.deliciasdelvalle.sigeppi.modelo;

/** DTO: insumo que no alcanza para una orden de producción. */
public class Faltante {
    private Insumo insumo;
    private double requerido;
    private double disponible;

    public Insumo getInsumo() { return insumo; }
    public void setInsumo(Insumo insumo) { this.insumo = insumo; }
    public double getRequerido() { return requerido; }
    public void setRequerido(double requerido) { this.requerido = requerido; }
    public double getDisponible() { return disponible; }
    public void setDisponible(double disponible) { this.disponible = disponible; }

    public double getCantidadFaltante() {
        return requerido - disponible;
    }
}
