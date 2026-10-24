package pe.deliciasdelvalle.sigeppi.dao;

import pe.deliciasdelvalle.sigeppi.modelo.RegistroAuditoria;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

public interface AuditoriaDAO {

    /** Registra la acción abriendo su propia conexión. */
    void registrar(Integer idUsuario, String ip, String modulo, String accion, String detalle) throws SQLException;
    /** Registra la acción dentro de una transacción que ya está abierta. */
    void registrar(Connection cn, Integer idUsuario, String ip, String modulo, String accion, String detalle) throws SQLException;

    List<RegistroAuditoria> listar(String modulo, String texto, int limite) throws SQLException;
}
