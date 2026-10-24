package pe.deliciasdelvalle.sigeppi.modelo;

/** Cantidad de un producto en la orden. */
public class DetalleOrden {
    private int idProducto;
    private String nombreProducto;
    private int cantidad;

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
