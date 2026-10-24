package pe.deliciasdelvalle.sigeppi.modelo;

import java.time.LocalDate;

/** DTO: una fila del reporte de ventas (tienda + día). */
public class ReporteVentaDTO {
    private String tienda;
    private LocalDate fecha;
    private int nroPedidos;
    private double totalVendido;

    public String getTienda() { return tienda; }
    public void setTienda(String tienda) { this.tienda = tienda; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public int getNroPedidos() { return nroPedidos; }
    public void setNroPedidos(int nroPedidos) { this.nroPedidos = nroPedidos; }
    public double getTotalVendido() { return totalVendido; }
    public void setTotalVendido(double totalVendido) { this.totalVendido = totalVendido; }
}
