package pe.deliciasdelvalle.sigeppi.modelo;

import java.util.ArrayList;
import java.util.List;

/** DTO: reporte completo que se muestra en la vista. */
public class ReporteVentas {
    private List<ReporteVentaDTO> filas = new ArrayList<>();
    private double totalGeneral;

    public List<ReporteVentaDTO> getFilas() { return filas; }
    public void setFilas(List<ReporteVentaDTO> filas) { this.filas = filas; }
    public double getTotalGeneral() { return totalGeneral; }
    public void setTotalGeneral(double totalGeneral) { this.totalGeneral = totalGeneral; }

    public boolean estaVacio() {
        return filas.isEmpty();
    }
}
