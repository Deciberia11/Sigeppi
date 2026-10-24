package pe.deliciasdelvalle.sigeppi.modelo;

/** Rol o perfil de acceso: VENDEDOR, JEFE_PRODUCCION, ENCARGADO_ALMACEN, ADMINISTRADOR. */
public class Rol {
    private int idRol;
    private String nombre;
    private String descripcion;

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public static final String VENDEDOR = "VENDEDOR";
    public static final String JEFE_PRODUCCION = "JEFE_PRODUCCION";
    public static final String ENCARGADO_ALMACEN = "ENCARGADO_ALMACEN";
    public static final String ADMINISTRADOR = "ADMINISTRADOR";
}
