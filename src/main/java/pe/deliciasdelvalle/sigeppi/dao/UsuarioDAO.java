package pe.deliciasdelvalle.sigeppi.dao;

import java.util.List;
import java.sql.SQLException;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;

public interface UsuarioDAO {

    Usuario buscarPorUsuario(String nombreUsuario) throws SQLException;
    void actualizarUltimoAcceso(int idUsuario) throws SQLException;

    Usuario buscarPorId(int idUsuario) throws SQLException;
    List<Usuario> listar() throws SQLException;
    boolean existeNombreUsuario(String nombreUsuario, int excluirId) throws SQLException;
    int insertar(Usuario usuario) throws SQLException;
    void actualizar(Usuario usuario) throws SQLException;
    void cambiarEstado(int idUsuario, boolean activo) throws SQLException;
    void actualizarContrasena(int idUsuario, String hash) throws SQLException;
}
