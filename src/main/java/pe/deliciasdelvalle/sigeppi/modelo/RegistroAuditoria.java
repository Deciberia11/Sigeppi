package pe.deliciasdelvalle.sigeppi.modelo;

import java.time.LocalDateTime;

/** Registro de la bitácora de auditoría (RNF-03). */
public class RegistroAuditoria {
    private long idAuditoria;
    private LocalDateTime fecha;
    private String usuario;
    private String ip;
    private String modulo;
    private String accion;
    private String detalle;

    public long getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(long idAuditoria) { this.idAuditoria = idAuditoria; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public String getFechaTexto() {
        return fecha == null ? "" : fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
