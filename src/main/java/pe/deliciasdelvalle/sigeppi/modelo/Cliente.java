package pe.deliciasdelvalle.sigeppi.modelo;

/** Cliente de la tienda (existe uno GENERICO para mostrador). */
public class Cliente {
    private int idCliente;
    private String nombres;
    private String telefono;
    private String tipoCliente = "FRECUENTE";

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }
}
